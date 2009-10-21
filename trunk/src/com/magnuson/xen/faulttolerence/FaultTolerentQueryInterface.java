package com.magnuson.xen.faulttolerence;

import com.magnuson.xen.*;
import java.util.*;

public interface FaultTolerentQueryInterface {

	public List<FaultSet> getFaultSets();
	public void addToFaultSet(VirtualMachine vm, FaultSet fs);
	public void removeFromFaultSet(VirtualMachine vm, FaultSet fs);
	public void addFaultSet(FaultSet fs);
	public void removeFaultSet(FaultSet fs);
	
}
