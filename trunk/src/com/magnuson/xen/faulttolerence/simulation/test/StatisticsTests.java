package com.magnuson.xen.faulttolerence.simulation.test;

import com.magnuson.xen.faulttolerence.simulation.*;
import junit.framework.TestCase;

public class StatisticsTests extends TestCase {

	public void testGetAvgVmUptime(){
		Statistics s = new Statistics();
		
		MomentStatistics ms = new MomentStatistics(5,0,3,0);
		s.addMomentStats(0, ms);
		ms = new MomentStatistics(4,1,2,1);
		s.addMomentStats(30, ms);
		ms = new MomentStatistics(5,0,3,0);
		s.addMomentStats(100, ms);
				
		double uptime = 30.0/100.0*5.0/5.0;
		uptime += 70.0/100.0*4.0/5.0;
		
		assertEquals(s.getAvgVmUptime(),uptime);
	}
}
