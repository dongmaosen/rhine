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
	
	public RhineInitThread() {
	
	}
	
	@Override
	public void run() {
		try {
			//0.连接ZK
			ZKUtility.connectForever();
			//1.初始化当前应的ZK的目录（永久节点）
			//1.1 当前工程的前缀（prefix）
			ZKUtility.createPath(ZKManager.getZKConfig().getPath(), CreateMode.PERSISTENT, ZKManager.getAcl(), false);
			//1.2 创建任务节点task
			ZKUtility.createPath(ZKManager.getZKConfig().getPath() + "/task", CreateMode.PERSISTENT, ZKManager.getAcl(), false);
			//1.3 创建策略节点strategy
			ZKUtility.createPath(ZKManager.getZKConfig().getPath() + "/strategy", CreateMode.PERSISTENT, ZKManager.getAcl(), false);
			//1.4 创建节点持久节点
			ZKUtility.createPath(ZKManager.getZKConfig().getPath() + "/node", CreateMode.PERSISTENT, ZKManager.getAcl(), false);			
			//2.创建当前运行节点管理器（要求唯一）
			RhineNodeManager.registNode();
			//3.下载并执行任务
			RhineScheduleManager.loadStrategyAndTask();
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
