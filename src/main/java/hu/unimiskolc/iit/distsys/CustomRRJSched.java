package hu.unimiskolc.iit.distsys;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.helpers.job.Job;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine.State;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine.StateChange;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.AlterableResourceConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ConstantConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ResourceConsumption;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ResourceConsumption.ConsumptionEvent;
import hu.mta.sztaki.lpds.cloud.simulator.io.Repository;
import hu.mta.sztaki.lpds.cloud.simulator.io.StorageObject;
import hu.mta.sztaki.lpds.cloud.simulator.io.VirtualAppliance;
import hu.unimiskolc.iit.distsys.interfaces.BasicJobScheduler;

public class CustomRRJSched implements BasicJobScheduler {
	
	public static ArrayList<VirtualMachine> vms = new ArrayList<VirtualMachine>();
	public static ArrayList<ComplexDCFJob> jobs = new ArrayList<ComplexDCFJob>();
	public static ArrayList<Boolean> jobsCompleted = new ArrayList<Boolean>();
	public static HashMap<VirtualMachine, ComplexDCFJob> vmAndJobs = new HashMap<VirtualMachine, ComplexDCFJob>();
	private IaaSService iaas;
	
	public CustomRRJSched() {
		
	}
	
	@Override
	public void setupVMset(Collection<VirtualMachine> vms) {
		int m = 7;
		m = 8;
	}

	@Override
	public void setupIaaS(IaaSService iaas) {
		this.iaas = iaas;
	}

	@Override
	public void handleJobRequestArrival(Job j) {
		ASD.handleJobRequestArrival(j, this.iaas);
	}
}

class ASD {
	
	public static void handleJobRequestArrival(Job j, IaaSService iaas) {
		
		ComplexDCFJob complexDCFJob = (ComplexDCFJob)j;
		Repository r = iaas.repositories.get(0);
		VirtualAppliance va = (VirtualAppliance) r.contents().iterator().next();
		
		double szam = j.nprocs * ExercisesBase.maxProcessingCap / (1.5 * j.nprocs);
		ConstantConstraints constantConstraints;
		if (j.nprocs < ExercisesBase.maxCoreCount) {
			constantConstraints = new ConstantConstraints(j.nprocs + 1, szam, 1024l * 1024 * 1024);
		}
		else {
			constantConstraints = new ConstantConstraints(j.nprocs, szam + 1024, 1024l * 1024 * 1024);
		}
		
		//iaas.listVMs().iterator().next().underProcessing.size()
		
		try {
			VirtualMachine vm = null;
			
			for (int i = 0; i < CustomRRJSched.vmAndJobs.size(); i++) {
				if (CustomRRJSched.jobsCompleted.get(i).booleanValue() &&
						CustomRRJSched.vms.get(i).getResourceAllocation().allocated.compareTo(constantConstraints) == -1 &&
						CustomRRJSched.vms.get(i).getState() == VirtualMachine.State.RUNNING) {
					vm = CustomRRJSched.vms.get(i);
					StateChange vmStateChange = new VMStateChange(complexDCFJob, iaas);
					vm.subscribeStateChange(vmStateChange);
					CustomConsumptionEvent customConsumptionEvent = new CustomConsumptionEvent(vm, complexDCFJob);
					complexDCFJob.startNowOnVM(vm, customConsumptionEvent);
					return;
				}
			}
			
			vm = iaas.requestVM(va, constantConstraints, r, 1)[0];
			StateChange vmStateChange = new VMStateChange(complexDCFJob, iaas);
			vm.subscribeStateChange(vmStateChange);
			CustomRRJSched.vms.add(vm);
			CustomRRJSched.vmAndJobs.put(vm, complexDCFJob);
			CustomRRJSched.jobs.add(complexDCFJob);
			CustomRRJSched.jobsCompleted.add(Boolean.FALSE);
		} catch (Exception e) {
			int m = 7;
			m = 8;
		}
	}
}

class VMStateChange implements StateChange {
	private ComplexDCFJob complexDCFJob;
	private IaaSService iaas;
	
	public VMStateChange(ComplexDCFJob complexDCFJob, IaaSService iaas) {
		this.complexDCFJob = complexDCFJob;
		this.iaas = iaas;
	}
	
	@Override
	public void stateChanged(VirtualMachine vm, State oldState, State newState) {
		if (newState == State.RUNNING) {
			try {
				CustomConsumptionEvent customConsumptionEvent = new CustomConsumptionEvent(vm, complexDCFJob);
				//ConsumptionEvent customConsumptionEvent = new CustomConsumptionEvent(vm);
				this.complexDCFJob.startNowOnVM(vm, (ConsumptionEvent)customConsumptionEvent);
				
			} catch (Exception e) {
				int m = 7;
				m = 8;
			}
		}
		else if (newState == State.DESTROYED) {
			//successfull
			if (this.complexDCFJob.getRealstopTime() >= 0) {
				int m = 7;
				m = 8;
			}
			else {
				ASD.handleJobRequestArrival(this.complexDCFJob, this.iaas);
			}
		}
	}
	
}