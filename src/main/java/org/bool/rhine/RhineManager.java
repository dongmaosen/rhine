package org.bool.rhine;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.zookeeper.KeeperException;


/**
 * 
 * Author: 不二   
 *
 * Copyright @ 2018
 * 
 */
public class RhineManager {
	/**
	 * 单例
	 */
	private RhineManager() {}
	/**
	 * 实例
	 */
	private static RhineManager rhineInstance = new RhineManager();
	/**
	 * 获得实例
	 * @return
	 */
	private static RhineManager getInstance() {
		return rhineInstance;
	}
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
				rhineInitThread = new RhineInitThread(getInstance());
				rhineInitThread.setName("rhineInitThread-" + rhineInitThread.getId());
				rhineInitThread.start();
			}
		} finally {
			lock.unlock();
		}
	}
	/**
	 * 创建当前活动的节点信息（临时节点）
	 * @throws Exception 
	 * @throws KeeperException 
	 */
	public void initTaskNodeManager() throws KeeperException, Exception {
		RhineNodeManager.registNode();
	}
	/**
	 * 加载策略和任务判断执行
	 */
	public void loadStrategyAndTask() {
		// TODO Auto-generated method stub
	}
}
