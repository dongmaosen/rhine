package org.bool.rhine;

import java.util.concurrent.Callable;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.bool.rhine.zookeeper.ZKTools;

/**
 * Init rhine
 *
 * Author: 不二   
 *
 * Copyright @ 2018
 * 
 */
public class RhineInitTask implements Callable<String>{
	
	public String call() {
		try {
			synchronized (RhineScheduleManager.lock) {				
				//0.连接ZK
				ZKTools.connectForever();
				//1.初始化当前应的ZK的目录（永久节点）
				//1.1 当前工程的前缀（prefix）
				ZKTools.createPath(ZKTools.getZKConfig().getPath(), CreateMode.PERSISTENT, ZKTools.getAcl(), false);
				//1.2 创建任务节点task
				ZKTools.createPath(ZKTools.getZKConfig().getPath() + "/task", CreateMode.PERSISTENT, ZKTools.getAcl(), false);
				//1.3 创建策略节点strategy
				ZKTools.createPath(ZKTools.getZKConfig().getPath() + "/strategy", CreateMode.PERSISTENT, ZKTools.getAcl(), false);
				//1.4 创建节点持久节点
				ZKTools.createPath(ZKTools.getZKConfig().getPath() + "/node", CreateMode.PERSISTENT, ZKTools.getAcl(), false);			
				//2.创建当前运行节点管理器（要求唯一）
				RhineNodeManager.registNode();
				//3.下载并执行任务
				RhineScheduleManager.loadStrategyAndTask();
			}
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return RhineConstants.INIT_SUCCESS;
	}

}
