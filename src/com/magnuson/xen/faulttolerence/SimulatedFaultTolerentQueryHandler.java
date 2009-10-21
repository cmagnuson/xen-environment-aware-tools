package com.magnuson.xen.faulttolerence;

import java.util.*;
import com.magnuson.xen.VirtualMachine;

public class SimulatedFaultTolerentQueryHandler implements FaultTolerentQueryInterface {

	private LinkedList<FaultSet> faultSets = new LinkedList<FaultSet>();
	
	public List<FaultSet> getFaultSets(){
		return faultSets;
	}
	
	public void addToFaultSet(VirtualMachine vm, FaultSet fs){
		fs.addVirtualMachine(vm);
	}
	
	public void removeFromFaultSet(VirtualMachine vm, FaultSet fs){
		fs.removeVirtualMachine(vm);
	}
	
	public void addFaultSet(FaultSet fs){
		faultSets.add(fs);
	}
	
	public void removeFaultSet(FaultSet fs){
		faultSets.remove(fs);
	}
}
