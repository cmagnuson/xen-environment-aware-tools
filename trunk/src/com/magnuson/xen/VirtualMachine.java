package com.magnuson.xen;

public class VirtualMachine {

	private String MACAddress;
	private String physicalMachineMACAddress;
	
	@SuppressWarnings("unused")
	private VirtualMachine(){}
	public VirtualMachine(String MACAddress){
		this.MACAddress = MACAddress;
	}
	
	public String toString(){
		return "VM:"+MACAddress;
	}
	
	public boolean equals(Object o){
		if(o instanceof VirtualMachine){
			return ((VirtualMachine)o).getMACAddress().equals(MACAddress);
		}
		return false;
	}

	public String getMACAddress() {
		return MACAddress;
	}

	public void setMACAddress(String mACAddress) {
		MACAddress = mACAddress;
	}

	public String getPhysicalMachineMACAddress() {
		return physicalMachineMACAddress;
	}

	public void setPhysicalMachineMACAddress(String physicalMachineMACAddress) {
		this.physicalMachineMACAddress = physicalMachineMACAddress;
	}
	
	
}
