package hu.unimiskolc.iit.distsys;

import java.util.List;

import org.apache.commons.lang3.RandomUtils;

import hu.mta.sztaki.lpds.cloud.simulator.iaas.IaaSService;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.PhysicalMachine;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.VMManager;
import hu.mta.sztaki.lpds.cloud.simulator.iaas.constraints.ResourceConstraints;
import hu.unimiskolc.iit.distsys.forwarders.IaaSForwarder;
import hu.unimiskolc.iit.distsys.interfaces.CloudProvider;

public class CustomCloudProvider7 implements CloudProvider, VMManager.CapacityChangeEvent<PhysicalMachine> {

	public IaaSService iaas;
	
	@Override
	public void setIaaSService(IaaSService iaas) {
		this.iaas = iaas;
		this.iaas.subscribeToCapacityChanges(this);
		((IaaSForwarder)this.iaas).setQuoteProvider(this);
	}
	
	@Override
	public double getPerTickQuote(ResourceConstraints rc) {
		if (rc.getRequiredCPUs() > 60) {
			return 0.000005;
		}
		if (rc.getRequiredCPUs() > 50) {
			return 0.000010;
		}
		if (rc.getRequiredCPUs() > 40) {
			return 0.000015;
		}
		if (rc.getRequiredCPUs() > 30) {
			return 0.000020;
		}
		if (rc.getRequiredCPUs() > 20) {
			return 0.000025;
		}
		if (rc.getRequiredCPUs() > 10) {
			return 0.000030;
		}
		
		return 0.000035;
	}

	@Override
	public void capacityChanged(ResourceConstraints newCapacity, List<PhysicalMachine> affectedCapacity) {
		
		int m = 7;
		m = 8;
		for (PhysicalMachine physicalMachine : affectedCapacity) {
			boolean newRegistration = this.iaas.isRegisteredHost(physicalMachine);
			if (!newRegistration) {
				try {
					for (PhysicalMachine pm : affectedCapacity) {
						// For every lost PM we buy a new one.
						this.iaas.registerHost(ExercisesBase.getNewPhysicalMachine(RandomUtils.nextDouble(7, 77)));
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		
		
	}
}
