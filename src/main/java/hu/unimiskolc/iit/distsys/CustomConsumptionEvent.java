package hu.unimiskolc.iit.distsys;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ResourceConsumption;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ResourceConsumption.ConsumptionEvent;

public class CustomConsumptionEvent implements ConsumptionEvent {

	private VirtualMachine vm;
	
	public CustomConsumptionEvent(VirtualMachine vm) {
		this.vm = vm;
	}
	
	@Override
	public void conComplete() {
		try {
			this.vm.destroy(true);
		} catch (Exception e) {
			int m = 7;
			m = 8;
		}
	}

	@Override
	public void conCancelled(ResourceConsumption problematic) {
		int m = 7;
		m = 8;
	}

}
