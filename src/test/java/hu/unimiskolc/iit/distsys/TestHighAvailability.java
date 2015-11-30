package hu.unimiskolc.iit.distsys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import hu.mta.sztaki.lpds.cloud.simulator.DeferredEvent;
import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.helpers.job.Job;
import hu.mta.sztaki.lpds.cloud.simulator.helpers.job.JobListAnalyser;
import hu.mta.sztaki.lpds.cloud.simulator.helpers.trace.random.RepetitiveRandomTraceGenerator;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService.IaaSHandlingException;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VMManager.VMManagementException;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine.State;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.AlterableResourceConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.io.Repository;
import hu.mta.sztaki.lpds.cloud.simulator.io.VirtualAppliance;

public class TestHighAvailability {
	
	@Before()
	public void asd(){
		System.setProperty("hu.unimiskolc.iit.distsys.RRJSched", "hu.unimiskolc.iit.distsys.CustomHA");
		
	}

	@Test()//(timeout = 30000)
	public void hatest() throws Exception {
		int[] successCounters = new int[Constants.availabilityLevels.length];
		int[] totalCounters = new int[Constants.availabilityLevels.length];
		final IaaSService myIaaS = ExercisesBase.getComplexInfrastructure(100);
		Repository r = myIaaS.repositories.get(0);
		VirtualAppliance va = (VirtualAppliance) r.contents().iterator().next();
		AlterableResourceConstraints totCaps = AlterableResourceConstraints.getNoResources();
		double maxNodeProcs = 0;
		for (PhysicalMachine pm : myIaaS.machines) {
			totCaps.singleAdd(pm.getCapacities());
			maxNodeProcs = Math.max(maxNodeProcs, pm.getCapacities().getRequiredCPUs());
		}
		// IaaS is prepared

		// Doing preevaluation of the infrastructure
		VirtualMachine test = myIaaS.requestVM(va, myIaaS.machines.get(0).getCapacities(), r, 1)[0];
		long preTime = Timed.getFireCount();
		Timed.simulateUntilLastEvent();
		long pastTime = Timed.getFireCount();
		long vmCreationTime = pastTime - preTime;
		test.destroy(true);
		Timed.simulateUntilLastEvent();
		Timed.resetTimed();
		// Preevaluation completed

		// Preparing the jobs for the VMs
		RepetitiveRandomTraceGenerator rrtg = new RepetitiveRandomTraceGenerator(ComplexDCFJob.class);
		// total number of jobs
		rrtg.setJobNum(1000);
		// joblist properties
		rrtg.setExecmin(10);
		rrtg.setExecmax(3600);
		rrtg.setMaxgap(0);
		rrtg.setMingap(0);
		rrtg.setMaxStartSpread(3600);
		rrtg.setMaxTotalProcs((int) totCaps.getRequiredCPUs());
		rrtg.setMinNodeProcs(1);
		rrtg.setMaxNodeprocs((int) maxNodeProcs);
		rrtg.setParallel(25);
		final List<Job> jobs = rrtg.getAllJobs();
		final long lastTermination = JobListAnalyser.getLastTerminationTime(jobs) * 1000 * 2;
		for (Job j : jobs) {
			int index = RandomUtils.nextInt(0, Constants.availabilityLevels.length);
			((ComplexDCFJob) j).setAvailabilityLevel(Constants.availabilityLevels[index]);
			totalCounters[index]++;
		}
		// Joblist is ready

		// Preparing the scheduling
		new JobtoVMScheduler(myIaaS, jobs);
		// Prepares the faulty PMs
		new FaultInjector(120000, 1 - Constants.pmAvailability, myIaaS);
		new DeferredEvent(lastTermination) {
			// Ensures that the fault injector code terminates
			@Override
			protected void eventAction() {
				FaultInjector.simulationisComplete = true;
			}
		};

		Timed.simulateUntilLastEvent();

		for (final Job j : jobs) {
			ComplexDCFJob jobconv = (ComplexDCFJob) j;
			if (j.getRealstopTime() >= 0) {
				successCounters[Arrays.binarySearch(Constants.availabilityLevels, jobconv.getAvailabilityLevel())]++;
				// More complex tests:
				// Should not allow too slow execution time
				Assert.assertTrue(
						"Every job should run faster or equal than it was originally expected but " + j
								+ " did not do so",
						j.getExectimeSecs() * 3 > j.getRealstopTime() - j.getRealqueueTime());
				// Should not allow too long queueing time
				Assert.assertTrue("Jobs should not queue more than a VM instantiation time but " + j + " did not do so",
						j.getRealqueueTime() < vmCreationTime * 3);
			}
		}

		for (int i = 0; i < Constants.availabilityLevels.length; i++) {
			System.out.println(Constants.availabilityLevels[i] + " " + successCounters[i] + " " + totalCounters[i]);
			Assert.assertEquals(
					"Jobs with availability level " + Constants.availabilityLevels[i]
							+ " did not get their expected qualities",
					Constants.availabilityLevels[i], (double) successCounters[i] / totalCounters[i],
					(1 - Constants.availabilityLevels[i]) * 0.5);
		}
	}
}
