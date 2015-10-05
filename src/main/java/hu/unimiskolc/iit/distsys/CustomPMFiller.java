package hu.unimiskolc.iit.distsys;

import java.util.List;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VMManager.VMManagementException;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ConstantConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ResourceConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.io.NetworkNode.NetworkException;
import hu.mta.sztaki.lpds.cloud.simulator.io.Repository;
import hu.mta.sztaki.lpds.cloud.simulator.io.StorageObject;
import hu.mta.sztaki.lpds.cloud.simulator.io.VirtualAppliance;
import hu.unimiskolc.iit.distsys.interfaces.FillInAllPMs;

public class CustomPMFiller implements FillInAllPMs {

	@Override
	public void filler(IaaSService iaas, int vmCount) {
		int pmCount = iaas.machines.size();
		double cpuCountTotal = 0;
		double kiosztottCpu = 0;
		int donePMCount = 0;
		int doneVM = 0;
		
		for (int i = 0; i < iaas.machines.size(); i++) {
			PhysicalMachine	pm = iaas.machines.get(i);
			cpuCountTotal += pm.getCapacities().getRequiredCPUs();
			if (!pm.isRunning()) {
				pm.turnon();
				Timed.simulateUntilLastEvent();
			}
		}
		
		for (int i = 0; i < iaas.machines.size(); i++) {
			PhysicalMachine	pm = iaas.machines.get(i);
			double cpuCountActualPM = pm.getCapacities().getRequiredCPUs();
			double kiosztottcpuCountActualPM = 0;
			
			for (int j = 0; j < cpuCountActualPM; j++) {
				try {
					if (pmCount - donePMCount >= vmCount - (doneVM + 1)) {
						VirtualAppliance va = getVA(iaas.repositories);
						Repository repository = getVASource(iaas.repositories, va);
					
						double cpu = pm.getCapacities().getRequiredCPUs();
						double maradekCpu = cpu - kiosztottcpuCountActualPM;
						kiosztottcpuCountActualPM += maradekCpu;
						kiosztottCpu += maradekCpu;
						
						ConstantConstraints cc = new ConstantConstraints(maradekCpu, 0, 1);
						iaas.requestVM(va, cc, repository, 1);
						Timed.simulateUntilLastEvent();
						doneVM++;
						break;
					}
					else {
						VirtualAppliance va = getVA(iaas.repositories);
						Repository repository = getVASource(iaas.repositories, va);
				
						ConstantConstraints cc = new ConstantConstraints(1, 0, 1);
						double cpu = cc.getRequiredCPUs();
						kiosztottCpu += cpu;
						kiosztottcpuCountActualPM += cpu;
						iaas.requestVM(va, cc, repository, 1);
						Timed.simulateUntilLastEvent();
						doneVM++;
					}
				}
				catch (Exception e) {
					int m = 7;
				}
			}
			donePMCount++;
			
			
//			if (pmCount - donePMCount == vmCount - doneVM ) {
//				try {
//					VirtualAppliance va = getVA(iaas.repositories);
//					Repository repository = getVASource(iaas.repositories, va);
//				
//					//ConstantConstraints cc = new ConstantConstraints(1, 0, 1);
//					kiosztottCpu += cc.getRequiredCPUs();
//					iaas.requestVM(va, cc, repository, 1);
//					Timed.simulateUntilLastEvent();
//					doneVM++;
//				}
//				catch (Exception e) {
//					int m = 7;
//				}
//			}
//			else {
//				
//			}
		}
		
//		for (int i = 0; i < vmCount; i++) {
//			
//		}
//		
//		for (int i = 0; i < iaas.machines.size(); i++) {
//			PhysicalMachine	pm = iaas.machines.get(i);
//			for (int j = 0; j < 10; j++) {
//				try {
//					VirtualAppliance va = getVA(iaas.repositories);
//					Repository repository = getVASource(iaas.repositories, va);
//					
//					ConstantConstraints cc = new ConstantConstraints(1, 0, 64);
//					
//					iaas.requestVM(va, pm.getCapacities(), repository, 1);
//					Timed.simulateUntilLastEvent();
//				}
//				catch (Exception e) {
//					int m = 7;
//				}
//			}
//		}
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
