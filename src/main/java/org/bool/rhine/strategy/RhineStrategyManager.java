package org.bool.rhine.strategy;

import org.apache.zookeeper.CreateMode;
import org.bool.rhine.zookeeper.ZKTools;

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
			while(!ZKTools.checkState()) {
				ZKTools.connect();
			}
			//创建并更新策略
			ZKTools.createUpdateData(ZKTools.getZKConfig().getPath() + "/strategy/" + strategy.getStrategyName(), jo.toString().getBytes(), CreateMode.PERSISTENT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
