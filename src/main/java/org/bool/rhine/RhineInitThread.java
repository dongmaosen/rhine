package org.bool.rhine;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.bool.rhine.zookeeper.ZKManager;
import org.bool.rhine.zookeeper.ZKUtility;

/**
 * 
 *
 * Author: 不二   
 *
 * Copyright @ 2018
 * 
 */
public class RhineInitThread extends Thread{

	
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
		try {
			//1.初始化当前应的ZK的目录（永久节点）
			//首先检查zk是否连接，未连接每隔2秒连接一次
			while (!ZKManager.checkState()) {
				Thread.sleep(2000);
			}
			//1.1 当前工程的前缀（prefix）
			ZKUtility.createPath(ZKManager.getZKConfig().getPath(), CreateMode.PERSISTENT, ZKManager.getAcl());
			//1.2 创建任务节点task
			ZKUtility.createPath(ZKManager.getZKConfig().getPath() + "/task", CreateMode.PERSISTENT, ZKManager.getAcl());
			//1.3 创建策略节点strategy
			ZKUtility.createPath(ZKManager.getZKConfig().getPath() + "/strategy", CreateMode.PERSISTENT, ZKManager.getAcl());
			//1.4 创建节点持久节点
			ZKUtility.createPath(ZKManager.getZKConfig().getPath() + "/node", CreateMode.PERSISTENT, ZKManager.getAcl());			
			//2.创建当前运行节点管理器（要求唯一）
			rm.initTaskNodeManager();
			//3.下载并执行任务
			rm.loadStrategyAndTask();
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
