package com.magnuson.xen.faulttolerence;

import com.magnuson.xen.*;

public class MigrationDecision {

	private VirtualMachine virtualMachine;
	private PhysicalMachine physicalSource;
	private PhysicalMachine physicalDestination;
	
	private MigrationDecision(){};
	public MigrationDecision(VirtualMachine vm, PhysicalMachine src, PhysicalMachine dest){
		virtualMachine = vm;
		physicalSource = src;
		physicalDestination = dest;
	}
	public VirtualMachine getVirtualMachine() {
		return virtualMachine;
	}
	public void setVirtualMachine(VirtualMachine virtualMachine) {
		this.virtualMachine = virtualMachine;
	}
	public PhysicalMachine getPhysicalSource() {
		return physicalSource;
	}
	public void setPhysicalSource(PhysicalMachine physicalSource) {
		this.physicalSource = physicalSource;
	}
	public PhysicalMachine getPhysicalDestination() {
		return physicalDestination;
	}
	public void setPhysicalDestination(PhysicalMachine physicalDestination) {
		this.physicalDestination = physicalDestination;
	}
	
	public String toString(){
		return virtualMachine.toString()+" src: "+physicalSource+" dst: "+physicalDestination;		 
	}
	
	
}
