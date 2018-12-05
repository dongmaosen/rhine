package org.bool.rhine;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * Author: 不二   
 *
 * Copyright @ 2018
 * 
 */
public class RhineStarter {
	/**
	 * lock for initialization
	 */
	private static Lock lock = new ReentrantLock();
	/**
	 * 客户端初始化线程
	 */
	private static RhineInitThread rhineInitThread;
	/**
	 * rhine初始化操作：客户端与ZK集群交互的开始，单独的线程
	 */
	public static void init() {

		//锁定防止多线程同时初始化
		lock.lock();
		try {
			if (rhineInitThread == null) {				
				rhineInitThread = new RhineInitThread();
				rhineInitThread.setName("rhineInitThread-" + rhineInitThread.getId());
				rhineInitThread.start();
			}
		} finally {
			lock.unlock();
		}
	}
}
