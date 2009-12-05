package com.magnuson.xen;

import java.util.*;
import org.apache.log4j.Logger;

public class SimulatedQueryHandler implements XenQueryHandlerInterface {

	static Logger log = Logger.getLogger(SimulatedQueryHandler.class);
	
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
		if(physicalMachines.containsValue(destination)){
			source.removeVirtualMachine(vm);
			destination.addVirtualMachine(vm);
			vm.setPhysicalMachineMACAddress(destination.getMACAddress());
			log.debug("Migrating VM:"+vm+" from:"+source+" to:"+destination);
			//MIGRATE BETWEEN BOXES SOMEWHERE HERE
		}
		else{
			//MIGRATION FAILS, DEST NOT AVAILABLE
		}
	}

	public void addVirtualMachine(VirtualMachine vm, PhysicalMachine pm){
		pm.addVirtualMachine(vm);
		vm.setPhysicalMachineMACAddress(pm.getMACAddress());
		virtualMachines.put(vm.getMACAddress(), vm);
		log.debug("Bringing VM:"+vm+" up on:"+pm);
	}

	public void removeVirtualMachine(VirtualMachine vm, PhysicalMachine pm){
		pm.removeVirtualMachine(vm);
		virtualMachines.remove(vm.getMACAddress());
		log.debug("Bringing VM:"+vm+" down on:"+pm);
	}

	public void addPhysicalMachine(PhysicalMachine pm){
		physicalMachines.put(pm.getMACAddress(), pm);
		log.debug("Bringing PM up:"+pm);
	}

	public void removePhysicalMachine(PhysicalMachine pm){
		physicalMachines.remove(pm.getMACAddress());
		log.debug("Bringing PM down:"+pm);
	}

	private HashMap<String,PhysicalMachine> physicalMachines = new HashMap<String,PhysicalMachine>();
	private HashMap<String,VirtualMachine> virtualMachines = new HashMap<String,VirtualMachine>();

}
