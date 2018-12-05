package org.bool.rhine.zookeeper;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

/**
 * ZK操作工具类
 * 
 * Author: 不二   
 *
 * Copyright @ 2018
 * 
 */
public class ZKUtility {
	/**
	 *  递归创建目录
	 * @throws Exception 
	 */
	public static void createPath(String path, CreateMode mode, List<ACL> acl, boolean watcher) throws KeeperException, Exception {
		//非空判断
		if(StringUtils.isBlank(path)) {
			return;
		}
		//盘符转换
		path = path.replace("\\", "/");
		//逐层创建目录
		String[] paths = path.split("/");
		String absPath = "";
		for (String pth : paths) {
			if (StringUtils.isNotBlank(pth)) {
				absPath = absPath + "/" + pth;
				if (ZKManager.getZooKeeper().exists(absPath, watcher) == null) {
					ZKManager.getZooKeeper().create(absPath, null, acl, mode);
				}
			}
		}
	}
	
	public static List<String> getChildren(String path, boolean watcher) throws KeeperException, Exception {
		return ZKManager.getZooKeeper().getChildren(path, watcher);
	}

	
	public static byte[] getData(String path, boolean watcher) throws KeeperException, InterruptedException {
		return ZKManager.getZooKeeper().getData(path, watcher, null);
	}
	
	/**
	 * 创建带数据的节点
	 * @param path
	 * @param data
	 * @param mode
	 * @throws Exception
	 */
	public static void create(String path, byte[] data, CreateMode mode) throws Exception {
		if (ZKManager.getZooKeeper().exists(path, false) == null) {			
			ZKManager.getZooKeeper().create(path, data, ZKManager.getAcl(), mode);
		}
	}

	public static void connectForever() {
		while (!ZKManager.checkState()) {
			ZKManager.connect();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
		
	}
}
