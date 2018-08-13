package org.bool.rhine.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;

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
		if (event.getState() == KeeperState.SyncConnected) {
			//连接成功
		} else if (event.getState() == KeeperState.Expired) {
			//会话超时
			ZKManager.reConnect();
		} else if (event.getState() == KeeperState.Disconnected) {
			//断开连接
			ZKManager.reConnect();
		} else if (event.getState() == KeeperState.AuthFailed) {
			//连接认证失败，重新连接无意义
		}
		if (event.getType() == EventType.NodeChildrenChanged) {
			
		}
	}

}
