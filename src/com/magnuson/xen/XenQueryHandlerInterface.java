package com.magnuson.xen;

import java.util.*;

public interface XenQueryHandlerInterface {

	public List<PhysicalMachine> getPhysicalMachines();
	public List<VirtualMachine> getVirtualMachines();
	public VirtualMachine getVirtualMachine(String MACAddress);
	public PhysicalMachine getPhysicalMachine(String MACAddress);
	public void migrateVirtualMachine(VirtualMachine vm, PhysicalMachine source, PhysicalMachine destination);
	public void addVirtualMachine(VirtualMachine vm, PhysicalMachine pm);
	public void removeVirtualMachine(VirtualMachine vm, PhysicalMachine pm);
	public void addPhysicalMachine(PhysicalMachine pm);
	public void removePhysicalMachine(PhysicalMachine pm);
	
}
