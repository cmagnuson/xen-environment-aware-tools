package com.magnuson.xen;

import java.util.*;
import org.apache.log4j.Logger;

public class Switch implements Gateway, NetworkDevice {

	private String MACAddress;
	private Gateway gateway;
	private boolean online = true;
	
	private List<NetworkDevice> devices = new LinkedList<NetworkDevice>();
	static Logger log = Logger.getLogger(Switch.class);

	public void addDevice(NetworkDevice d){
		devices.add(d);
	}
	public void removeDevice(NetworkDevice d){
		devices.remove(d);
	}
	
	
	
	public String getMACAddress() {
		return MACAddress;
	}
	public void setMACAddress(String mACAddress) {
		MACAddress = mACAddress;
	}
	public Gateway getGateway() {
		return gateway;
	}
	public void setGateway(Gateway gateway) {
		this.gateway = gateway;
	}
	public boolean isOnline() {
		return online;
	}
	public void setOnline(boolean online) {
		this.online = online;
	}
	public boolean hasInternetConnection(){
		if(!online){
			return false;
		}
		else{
			if(gateway instanceof InternetGateway){
				return true;
			}
			else if(gateway instanceof Switch){
				return ((Switch)gateway).hasInternetConnection();
			}
			else{
				//SHOULD NEVER HAPPEN
				log.error("NO VALID GATEWAY ASSIGNED TO SWITCH! PROGRAMMING ERROR!");
				return false;
			}
		}
	}
}
