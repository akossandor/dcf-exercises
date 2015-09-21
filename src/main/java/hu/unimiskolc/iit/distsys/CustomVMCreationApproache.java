package hu.unimiskolc.iit.distsys;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
		
	}

	@Override
	public void indirectVMCreation() throws Exception {
		
	}

	@Override
	public void migratedVMCreation() throws Exception {
		
	}

}
