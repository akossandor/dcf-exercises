package hu.unimiskolc.iit.distsys;

import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ResourceConsumption;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ResourceConsumption.ConsumptionEvent;

public class CustomConsumotionEventHA implements ConsumptionEvent {

	public boolean isCompleted = false;
	public ComplexDCFJob job;
	
	public CustomConsumotionEventHA(ComplexDCFJob job) {
		this.job = job;
	}
	
	
	public boolean GetIsCompleted() {
		return this.isCompleted;
	}

	@Override
	public void conComplete() {
		this.isCompleted = true;
	}

	@Override
	public void conCancelled(ResourceConsumption problematic) {
		int m = 7;
		m = 8;
		
	}	
}