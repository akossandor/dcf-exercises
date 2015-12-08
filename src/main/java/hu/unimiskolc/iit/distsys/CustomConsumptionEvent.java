package hu.unimiskolc.iit.distsys;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ResourceConsumption;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.resourcemodel.ResourceConsumption.ConsumptionEvent;

public class CustomConsumptionEvent implements ConsumptionEvent {

	private VirtualMachine vm;
	private ComplexDCFJob complexDCFJob;
	
	public CustomConsumptionEvent(VirtualMachine vm, ComplexDCFJob complexDCFJob) {
		this.vm = vm;
		this.complexDCFJob = complexDCFJob;
	}
	
	@Override
	public void conComplete() {
		try {
			//this.vm.destroy(true);
			for (int i = 0; i < CustomRRJSched.jobs.size(); i++) {
				if (CustomRRJSched.jobs.get(i) == this.complexDCFJob) {
				 	Boolean b = CustomRRJSched.jobsCompleted.get(i);
				 	b = Boolean.TRUE;
				}
			}
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
