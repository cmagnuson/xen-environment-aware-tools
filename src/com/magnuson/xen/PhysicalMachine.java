package com.magnuson.xen;

import java.util.*;

public class PhysicalMachine {

	private String MACAddress;
	private List<VirtualMachine> virtualMachines = new LinkedList<VirtualMachine>();
	
	private PhysicalMachine(){}
	public PhysicalMachine(String MACAddress){
		this.MACAddress = MACAddress;
	}
	
	public String toString(){
		return "PHYS:"+MACAddress;
	}
	
	public boolean equals(Object o){
		if(o instanceof PhysicalMachine){
			return ((PhysicalMachine)o).getMACAddress().equals(MACAddress);
		}
		return false;
	}
	
	public String getMACAddress() {
		return MACAddress;
	}
	public void setMACAddress(String mACAddress) {
		MACAddress = mACAddress;
	}
	public List<VirtualMachine> getVirtualMachines() {
		return virtualMachines;
	}
	public void addVirtualMachine(VirtualMachine vm){
		virtualMachines.add(vm);
	}
	public void removeVirtualMachine(VirtualMachine vm){
		virtualMachines.remove(vm);
	}
	
	
}
