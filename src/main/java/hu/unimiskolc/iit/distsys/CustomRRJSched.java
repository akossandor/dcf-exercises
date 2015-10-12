package hu.unimiskolc.iit.distsys;

import java.util.ArrayList;
import java.util.Collection;
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
	
	private ArrayList<VirtualMachine> vms;
	private IaaSService iaas;
	
	public CustomRRJSched() {
		this.vms = new ArrayList<VirtualMachine>();
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
		try {
			ComplexDCFJob complexDCFJob = (ComplexDCFJob)j;
			Repository r = iaas.repositories.get(0);
			VirtualAppliance va = (VirtualAppliance) r.contents().iterator().next();
			
			ConstantConstraints constantConstraints = new ConstantConstraints(j.nprocs + 10, 80, 1024l * 1024);
			
			VirtualMachine vm = iaas.requestVM(va, constantConstraints, r, 1)[0];
			
			StateChange vmStateChange = new VMStateChange(complexDCFJob);
			vm.subscribeStateChange(vmStateChange);
		} catch (Exception e) {
			int m = 7;
			m = 8;
		}
	}
}

class VMStateChange implements StateChange {
	private ComplexDCFJob complexDCFJob;
	
	public VMStateChange(ComplexDCFJob complexDCFJob) {
		this.complexDCFJob = complexDCFJob;
	}
	
	@Override
	public void stateChanged(VirtualMachine vm, State oldState, State newState) {
		if (newState == State.RUNNING) {
			try {
				ConsumptionEvent customConsumptionEvent = new CustomConsumptionEvent();
				this.complexDCFJob.startNowOnVM(vm, customConsumptionEvent);
				
			} catch (Exception e) {
				int m = 7;
				m = 8;
			}
		} 
	}
	
}