package hu.unimiskolc.iit.distsys;

import java.util.List;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VMManager.VMManagementException;
import hu.mta.sztaki.lpds.cloud.simulator.io.NetworkNode.NetworkException;
import hu.mta.sztaki.lpds.cloud.simulator.io.Repository;
import hu.mta.sztaki.lpds.cloud.simulator.io.StorageObject;
import hu.mta.sztaki.lpds.cloud.simulator.io.VirtualAppliance;
import hu.unimiskolc.iit.distsys.interfaces.FillInAllPMs;

public class CustomPMFiller implements FillInAllPMs {

	@Override
	public void filler(IaaSService iaas, int vmCount) {
		for (int i = 0; i < iaas.machines.size(); i++) {
			PhysicalMachine	pm = iaas.machines.get(i);
			if (!pm.isRunning()) {
				pm.turnon();
				Timed.simulateUntilLastEvent();
			}
		}
		
		for (int i = 0; i < vmCount; i++) {
			
		}
		
		for (int i = 0; i < iaas.machines.size(); i++) {
			PhysicalMachine	pm = iaas.machines.get(i);
			for (int j = 0; j < 10; j++) {
				try {
					VirtualAppliance va = getVA(iaas.repositories);
					Repository repository = getVASource(iaas.repositories, va);
					
					iaas.requestVM(va, pm.getCapacities(), repository, 1);
					Timed.simulateUntilLastEvent();
				}
				catch (Exception e) {
					int m = 7;
				}
			}
		}
	}
	
	private VirtualAppliance getVA(List<Repository> repositories) throws Exception {
		for (Repository item : repositories) {
			for (StorageObject itemm : item.contents()) {
				if (itemm instanceof VirtualAppliance) {
					return (VirtualAppliance) itemm;
				}
			}
		}
		
		throw new Exception("There is no VA.");
	}
	
	private Repository getVASource(List<Repository> repositories, VirtualAppliance va) throws Exception {
		for (Repository item : repositories) {
			for (StorageObject itemm : item.contents()) {
				if (itemm instanceof VirtualAppliance && itemm.equals(va)) {
					return item;
				}
			}
		}
		
		throw new Exception("There is no Repository.");
	}
	
}
