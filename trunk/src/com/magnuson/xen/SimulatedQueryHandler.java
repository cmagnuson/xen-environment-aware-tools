package com.magnuson.xen;

import java.util.*;

public class SimulatedQueryHandler implements XenQueryHandlerInterface {

	public List<PhysicalMachine> getPhysicalMachines(){
		return new LinkedList<PhysicalMachine>(physicalMachines.values());
	}
	
	public List<VirtualMachine> getVirtualMachines(){
		return new LinkedList<VirtualMachine>(virtualMachines.values());
	}
	
	public VirtualMachine getVirtualMachine(String MACAddress){
		return virtualMachines.get(MACAddress);
	}
	
	public PhysicalMachine getPhysicalMachine(String MACAddress){
		return physicalMachines.get(MACAddress);
	}
	
	public synchronized void migrateVirtualMachine(VirtualMachine vm, PhysicalMachine source, PhysicalMachine destination){
		source.removeVirtualMachine(vm);
		destination.addVirtualMachine(vm);
		vm.setPhysicalMachineMACAddress(destination.getMACAddress());
		//MIGRATE BETWEEN BOXES SOMEWHERE HERE
	}
	
	public void addVirtualMachine(VirtualMachine vm, PhysicalMachine pm){
		pm.addVirtualMachine(vm);
		vm.setPhysicalMachineMACAddress(pm.getMACAddress());
		virtualMachines.put(vm.getMACAddress(), vm);
	}
	
	public void removeVirtualMachine(VirtualMachine vm, PhysicalMachine pm){
		pm.removeVirtualMachine(vm);
		physicalMachines.remove(vm);
	}
	
	public void addPhysicalMachine(PhysicalMachine pm){
		physicalMachines.put(pm.getMACAddress(), pm);
	}
	
	public void removePhysicalMachine(PhysicalMachine pm){
		physicalMachines.remove(pm.getMACAddress());
	}
	
	private HashMap<String,PhysicalMachine> physicalMachines = new HashMap<String,PhysicalMachine>();
	private HashMap<String,VirtualMachine> virtualMachines = new HashMap<String,VirtualMachine>();

}
