package org.bool.rhine.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.bool.rhine.RhineNodeManager;
import org.bool.rhine.RhineScheduleManager;

/**
 * zookeeper watcher implemention
 *
 * Author: 不二  
 *
 * Copyright @ 2018
 * 
 */
public class RhineWatcher implements Watcher {

	public void process(WatchedEvent event) {
		//
		if (event.getState() == KeeperState.SyncConnected) {
			//连接成功
			//断开后重连情况下
			if (!RhineScheduleManager.getRunState()) {
				//修复节点失效后无节点问题
				try {
					RhineNodeManager.registNode();
				} catch (Exception e) {
					e.printStackTrace();
				}
				RhineScheduleManager.loadStrategyAndTask();
			}
		} else if (event.getState() == KeeperState.Expired) {
			RhineScheduleManager.clearAllTasks();
		} else if (event.getState() == KeeperState.Disconnected) {
			RhineScheduleManager.clearAllTasks();
			RhineScheduleManager.setRunState(false);
		} else if (event.getState() == KeeperState.AuthFailed) {
			//连接认证失败，重新连接无意义
		}
		if (event.getType() == EventType.NodeChildrenChanged) {
			RhineScheduleManager.loadStrategyAndTask();
			try {				
				if (ZKTools.getZooKeeper().exists(event.getPath(), false) != null) {
					ZKTools.getZooKeeper().getChildren(event.getPath(), true);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (event.getType() == EventType.NodeDataChanged || event.getType() == EventType.NodeCreated || event.getType() == EventType.NodeDeleted) {
			RhineScheduleManager.loadStrategyAndTask();
			try {
				if (ZKTools.getZooKeeper().exists(event.getPath(), true) != null) {
					ZKTools.getData(event.getPath(), true);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
