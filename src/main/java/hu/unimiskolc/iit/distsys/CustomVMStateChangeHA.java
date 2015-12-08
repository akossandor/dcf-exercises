package hu.unimiskolc.iit.distsys;

import java.util.ArrayList;

import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine.State;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine.StateChange;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ResourceConsumption;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ResourceConsumption.ConsumptionEvent;

public class CustomVMStateChangeHA implements StateChange {

	private ComplexDCFJob job;
	public CustomVMStateChangeHA(ComplexDCFJob job) {
		this.job = job;
	}
	
	public void stateChanged(VirtualMachine vm, State oldState, State newState) {
		if (newState == State.RUNNING) {
			try {
				CustomConsumotionEventHA customConsumptionEvent = new CustomConsumotionEventHA(this.job);
				this.job.startNowOnVM(vm, (ConsumptionEvent)customConsumptionEvent);
				
			} catch (Exception e) {
				int m = 7;
				m = 8;
			}
		}
	}
	
}
