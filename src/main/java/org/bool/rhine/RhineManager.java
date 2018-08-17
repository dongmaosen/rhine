package org.bool.rhine;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 *
 * Author: 不二   
 *
 * Copyright @ 2018
 * 
 */
public class RhineManager {
	
	private RhineManager() {}
	
	private static RhineManager rhineInstance = new RhineManager();
	
	public static RhineManager getInstance() {
		return rhineInstance;
	}
	/**
	 * lock for initialization
	 */
	private Lock lock = new ReentrantLock();
	/**
	 * 客户端初始化线程
	 */
	RhineInitThread rhineInitThread;
	/**
	 * rhine初始化操作：客户端与ZK集群交互的开始，单独的线程
	 */
	public void init() {
		if (rhineInitThread != null) {
			rhineInitThread.stopThread();
		}
		//锁定防止多线程同时初始化
		lock.lock();
		try {
			if (rhineInitThread == null || rhineInitThread.isStoped()) {				
				rhineInitThread = new RhineInitThread(this);
				rhineInitThread.setName("rhineInitThread-" + rhineInitThread.getId());
				rhineInitThread.start();
			}
		} finally {
			lock.unlock();
		}
	}
	/**
	 * 初始化基本路径数据
	 */
	public void initBaseData() {
		// TODO Auto-generated method stub
		
	}
}

/**
 * 初始化线程
 */
class RhineInitThread extends Thread {
	
	private boolean stop = false;
	
	/**
	 * 外部引用，用来通过外部manager的设置，控制线程内部逻辑
	 */
	RhineManager rm;
	
	public RhineInitThread(RhineManager rm) {
		this.rm = rm;
	}
	
	@Override
	public void run() {
		//初始化当前应用配置在ZK的目录
		rm.initBaseData();
	}
	
	public void stopThread() {
		stop = true;
	}
	/**
	 * 是否停止运行
	 * @return false-未停止 true-停止
	 */
	public boolean isStoped() {
		return stop;
	}
}
