package org.bool.rhine.zookeeper;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

/**
 * ZK工具
 *
 * Author: 不二   
 *
 * Copyright @ 2018
 * 
 */
public class ZKTools {
	
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
			ResourceBundle rb = ResourceBundle.getBundle("rhine");
			zkConfig.setConnectString(rb.getString("connectString"));
			zkConfig.setPath(rb.getString("prefix"));
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
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static List<ACL> getAcl() {
		return acl;
	}
	/**
	 * check zookeeper state (connected)
	 * @return
	 */
	public static boolean checkState() {
		return zookeeper == null ? false : zookeeper.getState() == States.CONNECTED;
	}
	/**
	 * 对外提供zk对象
	 * @return
	 * @throws Exception
	 */
	public static ZooKeeper getZooKeeper() {
		if(!checkState()){
			reConnect();
		}
		return zookeeper;
	}
	
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
				if (getZooKeeper().exists(absPath, watcher) == null) {
					getZooKeeper().create(absPath, null, acl, mode);
				}
			}
		}
	}
	
	public static List<String> getChildren(String path, boolean watcher) throws KeeperException, Exception {
		return getZooKeeper().getChildren(path, watcher);
	}

	
	public static byte[] getData(String path, boolean watcher) throws KeeperException, InterruptedException {
		return getZooKeeper().getData(path, watcher, null);
	}
	
	/**
	 * 创建带数据的节点
	 * @param path
	 * @param data
	 * @param mode
	 * @throws Exception
	 */
	public static void create(String path, byte[] data, CreateMode mode) throws Exception {
		if (getZooKeeper().exists(path, false) == null) {			
			getZooKeeper().create(path, data, ZKTools.getAcl(), mode);
		}
	}
	/**
	 * 强行删除节点（供删除任务和策略使用）
	 * @param path
	 * @throws Exception
	 */
	public static void delete(String path) throws Exception {
		getZooKeeper().delete(path, -1);
	}
	/**
	 * 可创建节点&更新数据
	 * @param path
	 * @param data
	 * @param mode
	 * @throws Exception
	 */
	public static void createUpdateData(String path, byte[] data, CreateMode mode) throws Exception {
		if (getZooKeeper().exists(path, false) == null) {			
			getZooKeeper().create(path, null, ZKTools.getAcl(), mode);
		}
		getZooKeeper().setData(path, data, -1);
	}

	public static void connectForever() {
		while (!checkState()) {
			connect();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
		
	}
}
