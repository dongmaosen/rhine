package org.bool.rhine.strategy;

import org.apache.zookeeper.CreateMode;
import org.bool.rhine.zookeeper.ZKManager;
import org.bool.rhine.zookeeper.ZKUtility;

import net.sf.json.JSONObject;

/**
 *
 *
 * Author: 不二   
 *
 * Copyright @ 2018
 * 
 */
public class RhineStrategyManager {
	/**
	 * 注册策略
	 * @param strategy
	 */
	public static void registStrategy(RhineStrategy strategy) {
		JSONObject jo = JSONObject.fromObject(strategy);
		try {
			//检查并连接
			while(!ZKManager.checkState()) {
				ZKManager.connect();
			}
			//创建带数据的节点
			ZKUtility.create(ZKManager.getZKConfig().getPath() + "/strategy/" + strategy.getStrategyName(), jo.toString().getBytes(), CreateMode.PERSISTENT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
