package org.bool.rhine.zookeeper;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
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
	public static void createPath(String path, CreateMode mode, List<ACL> acl) throws KeeperException, Exception {
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
				if (ZKManager.getZooKeeper().exists(absPath, false) == null) {
					ZKManager.getZooKeeper().create(absPath, null, acl, mode);
				}
			}
		}
	}
	
	/**
	 * get all sub path of given path
	 * @param path
	 * @throws Exception 
	 */
	public static List<String> getPathTree(String path) throws KeeperException, Exception {
		if(ZKManager.getZooKeeper().exists(path, false) == null) {
			return new ArrayList<String>();
		}
		List<String> nodes = new ArrayList<String>();
		//此方法的根节点
		nodes.add(path);
		int index = 0;
		while (index < nodes.size()) {
			String cpath = nodes.get(index);
			List<String> children = ZKManager.getZooKeeper().getChildren(cpath, false);
			if (!"/".equals(cpath)) {
				//如果不是根目录，则末尾添加目录
				cpath += "/";
			}
			//按字母顺序升序
			Collections.sort(children);
			for (int j = 0; j < children.size(); j++) {
				nodes.add(index + 1 + j, cpath + children.get(j));
			}
			index++;
		}
		return nodes;
	}
	
	/**
	 * 删除一个目录下所有的目录
	 * @throws Exception 
	 */
	public static void deletePath(String path) throws Exception {
		List<String> paths = getPathTree(path);
		if (paths != null) {
			for (int i = paths.size() - 1; i >= 0; i--) {
				ZKManager.getZooKeeper().delete(path, -1);
			}
		}
	}
	
	/**
	 *  将参数目录下所有的目录和值，存储到buffer中
	 * @param path
	 * @param buffer
	 * @throws Exception 
	 * @throws KeeperException 
	 */
	public static void printTree(String path, StringBuffer buffer) throws KeeperException, Exception {
		List<String> paths = getPathTree(path);
		Stat stat = new Stat();
		for (String p : paths) {
			byte[] data = ZKManager.getZooKeeper().getData(p, false, stat);
			if (data == null) {
				buffer.append(p).append("\n");
			} else {
				buffer.append(p).append("[v=").append(stat.getVersion())
					  .append(",data=").append(new String(data)).append("]").append("\n");
			}
			
		}
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
}
