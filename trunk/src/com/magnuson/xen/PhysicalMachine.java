package com.magnuson.xen;

import java.util.*;
import com.magnuson.xen.faulttolerence.*;

public class PhysicalMachine implements NetworkDevice {

	private String MACAddress;
	private List<VirtualMachine> virtualMachines = new LinkedList<VirtualMachine>();
	private double mtbf;
	private Switch networkSwitch;
	
	@SuppressWarnings("unused")
	private PhysicalMachine(){}
	public PhysicalMachine(String MACAddress, Switch networkSwitch, double mtbf){
		this.MACAddress = MACAddress;
		this.mtbf = mtbf;
		this.networkSwitch = networkSwitch;
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
//	public double getRisk(FaultSet fs){		
//		return (double)getFSVms(fs)*(mtbf*networkSwitch.getRisk());
//	}
	public double getRiskPlus(FaultSet fs, int totalVms){
		if(totalVms<=0){
			return 0;
		}
		return (double)(totalVms)*(1 - (mtbf*networkSwitch.getRisk()));
	}
	
	public int getFSVms(FaultSet fs){
		int vmCount = 0;
		for(VirtualMachine vm: fs.getVirtualMachines()){
			if(virtualMachines.contains(vm)){
				vmCount++;
			}
		}
		return vmCount;
	}
	public double getMtbf(){
		return mtbf;
	}
	
	public Switch getSwitch(){
		return networkSwitch;
	}
}
