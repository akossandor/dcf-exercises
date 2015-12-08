package hu.unimiskolc.iit.distsys;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ResourceConsumption;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ResourceConsumption.ConsumptionEvent;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine;

public class CustomConsumotionEventHA implements ConsumptionEvent {

	public boolean isCompleted = false;
	public ComplexDCFJob job;
	
	
	public CustomConsumotionEventHA(ComplexDCFJob job) {
		this.job = job;
	}
	
	@Override
	public void conComplete() {
		this.isCompleted = true;
		for (Entry<ComplexDCFJob, ArrayList<VirtualMachine>> entry : CustomHA.jobAndItsVMs.entrySet())
		{
		    if (entry.getKey().equals(this.job)) {
				for(VirtualMachine actualVM : entry.getValue()) {
					try {
						actualVM.destroy(true);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
		}
	}

	@Override
	public void conCancelled(ResourceConsumption problematic) {
		int m = 7;
		m = 8;
		
	}	
}