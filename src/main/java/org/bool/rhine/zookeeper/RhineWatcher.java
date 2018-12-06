package org.bool.rhine.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
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
		//方便测试
		System.out.println(event.getPath());
		System.out.println(event.getType());
		//
		if (event.getState() == KeeperState.SyncConnected) {
			//连接成功
		} else if (event.getState() == KeeperState.Expired) {
			ZKTools.connectForever();
			RhineScheduleManager.loadStrategyAndTask();
		} else if (event.getState() == KeeperState.Disconnected) {
			ZKTools.connectForever();
			RhineScheduleManager.loadStrategyAndTask();
		} else if (event.getState() == KeeperState.AuthFailed) {
			//连接认证失败，重新连接无意义
		}
		if (event.getType() == EventType.NodeChildrenChanged || event.getType() == EventType.NodeDataChanged 
				|| event.getType() == EventType.NodeCreated || event.getType() == EventType.NodeDeleted) {
			RhineScheduleManager.loadStrategyAndTask();
			try {				
				if (ZKTools.getZooKeeper().exists(event.getPath(), false) != null) {
					ZKTools.getZooKeeper().getChildren(event.getPath(), true);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
