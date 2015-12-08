package hu.unimiskolc.iit.distsys;

import java.util.ArrayList;

import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine.State;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine.StateChange;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ResourceConsumption;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ResourceConsumption.ConsumptionEvent;

public class CustomVMStateChangeHA implements StateChange {

	private CustomHA customHA;
	public CustomVMStateChangeHA(CustomHA customHA) {
		this.customHA = customHA;
	}
	
	public void stateChanged(VirtualMachine vm, State oldState, State newState) {
		if (newState == State.RUNNING) {
			try {
				int index = this.customHA.vms.indexOf(vm);
				
				ComplexDCFJob complexDCFJob = (ComplexDCFJob) this.customHA.events.get(index).job;
				
				CustomConsumptionEvent customConsumptionEvent = new CustomConsumptionEvent(vm, complexDCFJob);
				complexDCFJob.startNowOnVM(vm, (ConsumptionEvent)customConsumptionEvent);
				
			} catch (Exception e) {
				int m = 7;
				m = 8;
			}
		}
	}
	
}
