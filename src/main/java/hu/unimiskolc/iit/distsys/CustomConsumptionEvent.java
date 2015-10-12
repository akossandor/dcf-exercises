package hu.unimiskolc.iit.distsys;

import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ResourceConsumption;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ResourceConsumption.ConsumptionEvent;

public class CustomConsumptionEvent implements ConsumptionEvent {

	@Override
	public void conComplete() {
		int m = 7;
		m = 8;
	}

	@Override
	public void conCancelled(ResourceConsumption problematic) {
		int m = 7;
		m = 8;
	}

}
