package org.bool.rhine;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * Author: 不二   
 *
 * Copyright @ 2018
 * 
 */
public class RhineBootStrap {
	/**
	 * lock for initialization
	 */
	private static Lock lock = new ReentrantLock();
	/**
	 * 客户端初始化线程
	 */
	private static RhineInitTask rhineInitThread;
	/**
	 * rhine初始化操作：客户端与ZK集群交互的开始，单独的线程
	 * @return 
	 */
	public static Future<String> start() {
		//锁定防止多线程同时初始化
		lock.lock();
		try {
			if (rhineInitThread == null) {				
				ExecutorService es = Executors.newSingleThreadExecutor();
				return es.submit(new RhineInitTask());
			}
		} finally {
			lock.unlock();
		}
		return null;
	}
}
