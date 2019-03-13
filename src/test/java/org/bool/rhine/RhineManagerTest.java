package org.bool.rhine;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
	public void rhineInitTest() throws InterruptedException, ExecutionException {
		Future<String> future = RhineBootStrap.start();
		while (!future.isDone()) {
			System.out.println(future.get());
		}
		Thread.sleep(60 * 60 * 1000);
	}
}
