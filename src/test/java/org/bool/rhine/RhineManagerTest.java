package org.bool.rhine;

import org.junit.Test;

/**
 *
 *
 * Author: 不二   
 *
 * Copyright @ 2018
 * 
 */
public class RhineManagerTest {
	
	@Test
	public void rhineInitTest() throws InterruptedException {
		RhineStarter.init();
		Thread.sleep(60 * 60 * 1000);
	}
}
