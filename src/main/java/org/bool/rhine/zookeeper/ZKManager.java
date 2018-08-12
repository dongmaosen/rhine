package org.bool.rhine.zookeeper;

import java.util.ResourceBundle;

import org.apache.zookeeper.ZooKeeper;

/**
 * ZK管理器，初始化配置等
 *
 * Author: 不二   
 *
 * Copyright @ 2018
 * 
 */
public class ZKManager {
	
	/**
	 * ZK的配置
	 */
	private static ZKConfig zkConfig;
	
	/**
	 * ZK客户端对象
	 */
	private static ZooKeeper zookeeper;
	
	/**
	 * initialize zkConfig
	 */
	public static void initConfig() {
		if (zkConfig == null) {
			zkConfig = new ZKConfig();
			ResourceBundle rb = ResourceBundle.getBundle("zk.properties");
			zkConfig.setConnectString(rb.getString("connectString"));
			zkConfig.setPath(rb.getString("path"));
			zkConfig.setSessionTimeout(Integer.parseInt(rb.getString("sessionTimeout")));
			zkConfig.setUserName(rb.getString("userName"));
			zkConfig.setPassword(rb.getString("password"));
		}
	}
	
	public static ZKConfig getZKConfig() {
		if (zkConfig == null) {
			initConfig();
		}
		return zkConfig;
	}
}
