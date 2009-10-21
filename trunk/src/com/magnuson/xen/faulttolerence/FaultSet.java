package com.magnuson.xen.faulttolerence;

import java.util.*;
import com.magnuson.xen.*;

public class FaultSet {

	private LinkedList<VirtualMachine> virtualMachines = new LinkedList<VirtualMachine>();

	public void addVirtualMachine(VirtualMachine vm){
		virtualMachines.add(vm);
	}
	
	public void removeVirtualMachine(VirtualMachine vm){
		virtualMachines.remove(vm);
	}
	
	public List<VirtualMachine> getVirtualMachines(){
		return virtualMachines;
	}
	
	public int size(){
		return virtualMachines.size();
	}
}
