package org.bool.rhine.task;

import org.junit.Test;

/**
 *
 *
 * Author: 不二   
 *
 * Copyright @ 2018
 * 
 */
public class RhineTaskManagerTest {
	@Test
	public void registJobTest() {
		RhineQuartzDemoJob rqd = new RhineQuartzDemoJob();
		rqd.setName("rhineTestJob");
		RhineTaskManager.registQuartzJob(rqd);
	}
}
