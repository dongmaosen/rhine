package org.bool.rhine.zookeeper;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

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
	 * 监听器
	 */
	private static Watcher watcher = new RhineWatcher();
	
	/**
	 *  连接用的锁
	 */
	private static Lock connLock = new ReentrantLock();
	
	/**
	 *  一些安全策略
	 */
	private static List<ACL> acl = new ArrayList<ACL>();
	/**
	 * initialize zkConfig
	 */
	private static void initConfig() {
		if (zkConfig == null) {
			zkConfig = new ZKConfig();
			ResourceBundle rb = ResourceBundle.getBundle("rhine_zk");
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
	/**
	 * 创建与zk集群的连接
	 */
	public static void connect() {
		try {
			connLock.lock();
			initZookeeper();
		} finally {
			connLock.unlock();
		}
	}
	
	public static void reConnect(){
		if (zookeeper != null) {
			close();
			zookeeper = null;
		}
		connect();
	}
	
	private static void close() {
		try {
			if (zookeeper != null) {
				zookeeper.close();				
			}
		} catch (InterruptedException e) {
			//
		}
	}

	/**
	 * 初始化客户端ZK对象
	 */
	private static void initZookeeper() {
		//首次初始化
		if (zookeeper == null) {
			initConfig();
			try {
				zookeeper = new ZooKeeper(zkConfig.getConnectString(), zkConfig.getSessionTimeout(), watcher);
				String authString = zkConfig.getUserName() + ":" + zkConfig.getPassword();
				//采用digest这种schema
				zookeeper.addAuthInfo("digest", authString.getBytes());
				//安全策略(用户名和密码的读写&任何人都可读的权限)
				acl.add(new ACL(ZooDefs.Perms.ALL, new Id("digest", DigestAuthenticationProvider.generateDigest(authString))));
				acl.add(new ACL(ZooDefs.Perms.READ, Ids.ANYONE_ID_UNSAFE));
			} catch (IOException e) {
				//TODO
			} catch (NoSuchAlgorithmException e) {
				//TODO
			}
		}
	}
}
