package hu.unimiskolc.iit.distsys;

import java.util.ArrayList;
import java.util.Collection;

import hu.mta.sztaki.lpds.cloud.simulator.helpers.job.Job;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VMManager.VMManagementException;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine.StateChange;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ConstantConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.io.NetworkNode.NetworkException;
import hu.mta.sztaki.lpds.cloud.simulator.io.Repository;
import hu.mta.sztaki.lpds.cloud.simulator.io.VirtualAppliance;
import hu.unimiskolc.iit.distsys.interfaces.BasicJobScheduler;
import hu.unimiskolc.iit.distsys.solution.SolutionHA;

public class CustomHA implements BasicJobScheduler {
	                                                     //0.75, 0.9, 0.95, 0.99
	public static int[] parallelVMsForAvaibilityLevels = { 2,    3,   5,    14 };
	//public static int[] parallelVMsForAvaibilityLevels = { 8,    9,   11,    20 };
	public IaaSService iaas;
	public Repository repository;
	public VirtualAppliance va;
	public ArrayList<VirtualMachine> vms = new ArrayList<VirtualMachine>();
	public ArrayList<CustomConsumotionEventHA> events = new ArrayList<CustomConsumotionEventHA>();
	
	@Override
	public void setupVMset(Collection<VirtualMachine> vms) {
		int m = 7;
		m = 8;
	}

	@Override
	public void setupIaaS(IaaSService iaas) {
		this.iaas = iaas;
		repository = iaas.repositories.get(0);
		va = (VirtualAppliance) repository.contents().iterator().next();
	}

	@Override
	public void handleJobRequestArrival(Job j) {
		ComplexDCFJob job = (ComplexDCFJob)j;
		double avaibilityLevel = job.getAvailabilityLevel();
		
		int necessaryVM = this.getVmCount(job.getAvailabilityLevel());
		
		for (int i = 0; i < necessaryVM; i++) {			
			ConstantConstraints cc = new ConstantConstraints(j.nprocs, ExercisesBase.minProcessingCap, ExercisesBase.minMem / j.nprocs);
			try {
				VirtualMachine vm = this.iaas.requestVM(this.va, cc, this.repository, 1)[0];
				vm.subscribeStateChange(new CustomVMStateChangeHA(this));
				vms.add(vm);
				CustomConsumotionEventHA event = new CustomConsumotionEventHA(job);
				events.add(event);
				
			} catch (Exception e) {
				int m = 7;
				e.printStackTrace();
			}
		}
		
		int m = 7;
		m = 8;
	}
	
	private int getVmCount(double avaibilityLevel){
		for (int i = 0; i < Constants.availabilityLevels.length; i++) {
			if (Constants.availabilityLevels[i] == avaibilityLevel) {
				return parallelVMsForAvaibilityLevels[i]; 
			}
		}
		
		return -1;
		//throw new Exception("From getVmCount: There is no avaibilityLevel.");
	}

}
