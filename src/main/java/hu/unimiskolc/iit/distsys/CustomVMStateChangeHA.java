package hu.unimiskolc.iit.distsys;

import java.util.ArrayList;

import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine.State;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine.StateChange;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ResourceConsumption;

public class CustomVMStateChangeHA implements StateChange {

	private CustomHA customHA;
	public CustomVMStateChangeHA(CustomHA customHA) {
		this.customHA = customHA;
	}
	
	@Override
	public void stateChanged(VirtualMachine vm, State oldState, State newState) {
		int index = this.customHA.vms.indexOf(vm);
		
		ComplexDCFJob job = (ComplexDCFJob) this.customHA.events.get(index).job;
		
		
		
	}

}
