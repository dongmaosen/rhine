package org.bool.rhine.zookeeper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.ACL;

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
}
