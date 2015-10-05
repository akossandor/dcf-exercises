package hu.unimiskolc.iit.distsys;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Assert;

import hu.mta.sztaki.lpds.cloud.simulator.Timed;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VirtualMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine.ResourceAllocation;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VMManager.VMManagementException;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ConstantConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ResourceConstraints;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.vmscheduling.Scheduler;
import hu.mta.sztaki.lpds.cloud.simulator.io.Repository;
import hu.mta.sztaki.lpds.cloud.simulator.io.StorageObject;
import hu.mta.sztaki.lpds.cloud.simulator.io.VirtualAppliance;
import hu.mta.sztaki.lpds.cloud.simulator.io.NetworkNode.NetworkException;
import hu.unimiskolc.iit.distsys.forwarders.IaaSForwarder;
import hu.unimiskolc.iit.distsys.interfaces.VMCreationApproaches;

public class CustomVMCreationApproache extends ExercisesBase implements VMCreationApproaches {
	public static final long aSecond = 1000; // in ms
	
	final static int reqcores = 2, reqProcessing = 3, reqmem = 4,
			reqond = 2 * (int) aSecond, reqoffd = (int) aSecond;
	final static ResourceConstraints smallConstraints = new ConstantConstraints(
			reqcores / 2, reqProcessing, reqmem / 2);
	final static ResourceConstraints overCPUConstraints = new ConstantConstraints(
			reqcores * 2, reqProcessing, reqmem);
	final static ResourceConstraints overMemoryConstraints = new ConstantConstraints(
			reqcores, reqProcessing, reqmem * 2);
	final static ResourceConstraints overProcessingConstraints = new ConstantConstraints(
			reqcores, reqProcessing * 2, reqmem);
	final static String pmid = "TestingPM";
	
	private VirtualMachine[] requestVMs(PhysicalMachine pm, ResourceConstraints rc,
			VirtualAppliance va, int count) throws VMManagementException,
			NetworkException {
		Repository repository = pm.localDisk;
		Collection<StorageObject> contents = repository.contents();
		Iterator<StorageObject> iterator = contents.iterator();
		StorageObject storageObject = iterator.next();

		return pm.requestVM(va == null ? (VirtualAppliance) storageObject : va, rc, pm.localDisk, count);
	}
	
	@Override
	public void directVMCreation() throws Exception {
		PhysicalMachine pm = ExercisesBase.getNewPhysicalMachine();
		if (!pm.isRunning()) {
			VirtualAppliance va = new VirtualAppliance("asd", 777, 0, false, pm.localDisk.getMaxStorageCapacity() / 5);
			pm.localDisk.registerObject(va);
			
			pm.turnon();
			Timed.simulateUntilLastEvent();
		
		
			VirtualMachine[] vms = requestVMs(pm, smallConstraints, null, 2);
			Timed.simulateUntilLastEvent();
		}
	}

	@Override
	public void twoPhaseVMCreation() throws Exception {
		PhysicalMachine pm = ExercisesBase.getNewPhysicalMachine();
		if (!pm.isRunning()) {
			VirtualAppliance va = new VirtualAppliance("asd", 777, 0, false, pm.localDisk.getMaxStorageCapacity() / 5);
			pm.localDisk.registerObject(va);
			
			pm.turnon();
			Timed.simulateUntilLastEvent();
			
			VirtualMachine virtualMachine1 = new VirtualMachine(va == null ? (VirtualAppliance) pm.localDisk
					.contents().iterator().next() : va);
			
			ResourceAllocation resourceAllocation1 = pm.allocateResources(smallConstraints, true,
					PhysicalMachine.defaultAllocLen);
			
			pm.deployVM(virtualMachine1, resourceAllocation1, pm.localDisk);
			Timed.simulateUntilLastEvent();
			
			VirtualMachine virtualMachine2 = new VirtualMachine(va == null ? (VirtualAppliance) pm.localDisk
					.contents().iterator().next() : va);
			
			ResourceAllocation resourceAllocation2 = pm.allocateResources(smallConstraints, true,
					PhysicalMachine.defaultAllocLen);
			
			pm.deployVM(virtualMachine2, resourceAllocation2, pm.localDisk);
			Timed.simulateUntilLastEvent();
		}
	}

	@Override
	public void indirectVMCreation() throws Exception {
		IaaSService iaasService = ExercisesBase.getNewIaaSService();
		PhysicalMachine pm = ExercisesBase.getNewPhysicalMachine();
		VirtualAppliance va = new VirtualAppliance("asd", 777, 0, false, pm.localDisk.getMaxStorageCapacity() / 5);
		if (!pm.isRunning()) {
			pm.localDisk.registerObject(va);
			
			pm.turnon();
			Timed.simulateUntilLastEvent();
		}
		
		iaasService.registerHost(pm);
		iaasService.registerRepository(pm.localDisk);
		
		iaasService.requestVM((VirtualAppliance) iaasService.repositories.get(0).contents().iterator().next(),
				iaasService.getCapacities(), iaasService.repositories.get(0), 1);
		Timed.simulateUntilLastEvent();
	}

	@Override
	public void migratedVMCreation() throws Exception {
		VirtualMachine[] vms = null;
		PhysicalMachine pm1 = ExercisesBase.getNewPhysicalMachine();
		PhysicalMachine pm2 = ExercisesBase.getNewPhysicalMachine();
		VirtualAppliance va = new VirtualAppliance("asd", 777, 0, false, pm1.localDisk.getMaxStorageCapacity() / 5);
		if (!pm1.isRunning()) {
			pm1.localDisk.registerObject(va);
			
			pm1.turnon();
			Timed.simulateUntilLastEvent();
		
		
			vms = requestVMs(pm1, smallConstraints, null, 1);
			Timed.simulateUntilLastEvent();
		}
		
		if (!pm2.isRunning()) {
			pm2.localDisk.registerObject(va);
			
			pm2.turnon();
			Timed.simulateUntilLastEvent();
		}
		
		ResourceAllocation resourceAllocation2 = pm2.allocateResources(smallConstraints, true,
				PhysicalMachine.defaultAllocLen);
		pm1.migrateVM(vms[0], pm2);
		Timed.simulateUntilLastEvent();
	}

	public void feladat01() throws Exception {
		int countOfPMS = 10;
		IaaSService iaasService = ExercisesBase.getNewIaaSService();
		ArrayList<PhysicalMachine> pms = new ArrayList<PhysicalMachine>();
		
		for (int i = 0; i < countOfPMS; i++) {
			PhysicalMachine pm = ExercisesBase.getNewPhysicalMachine();
			pms.add(pm);
			
			VirtualAppliance va = new VirtualAppliance("asd" + i, 777, 0, false, pm.localDisk.getMaxStorageCapacity() / 12);
			if (!pm.isRunning()) {
				pm.localDisk.registerObject(va);
				
				pm.turnon();
				Timed.simulateUntilLastEvent();
			}
			
			iaasService.registerHost(pm);
			iaasService.registerRepository(pm.localDisk);
			
			for (int j = 0; j < 10; j++) {
				iaasService.requestVM((VirtualAppliance) iaasService.repositories.get(i).contents().iterator().next(),
						iaasService.getCapacities(), iaasService.repositories.get(i), 1);
				Timed.simulateUntilLastEvent();
			}
		}
	}
}
