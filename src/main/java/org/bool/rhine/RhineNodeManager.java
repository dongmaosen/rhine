package org.bool.rhine;

import java.net.InetAddress;
import java.util.UUID;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.bool.rhine.zookeeper.ZKTools;

/**
 * 当前运行节点管理
 *
 * Author: 不二   
 *
 * Copyright @ 2018
 * 
 */
public class RhineNodeManager {
	
	private static String nodeName; 
	
	/**
	 * 返回当前节点的名字
	 * @return
	 */
	public static String getNodeName() {
		return nodeName;
	}
	
	/**
	  * 注册当前jvm到zk，参与执行任务
	 * @throws Exception 
	 * @throws KeeperException 
	 */
	public static void registNode() throws KeeperException, Exception {
		if (nodeName == null) {			
			nodeName = InetAddress.getLocalHost().getHostAddress() + "#" + UUID.randomUUID().toString().replaceAll("-", "") + "#";
		}
		ZKTools.createPath(ZKTools.getZKConfig().getPath() + "/node/" + nodeName, CreateMode.EPHEMERAL_SEQUENTIAL, ZKTools.getAcl(), false);
	}
	
	
	

}
