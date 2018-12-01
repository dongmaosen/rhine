package org.bool.rhine.task;

import org.apache.zookeeper.CreateMode;
import org.bool.rhine.zookeeper.ZKManager;
import org.bool.rhine.zookeeper.ZKUtility;

import net.sf.json.JSONObject;

/**
 * 任务管理类
 *
 * Author: 不二   
 *
 * Copyright @ 2018
 * 
 */
public class RhineTaskManager {
	/**
	 * 注册Quartz类型的Job
	 */
	public static void registQuartzJob(RhineQuartzJob job) {
		JSONObject jo = new JSONObject();
		jo.put("job_name", job.getName());
		jo.put("class_name", job.getClassName());
		try {
			//检查并连接
			while (!ZKManager.checkState()) {
				ZKManager.connect();
			}
			//创建带数据的节点
			ZKUtility.create(ZKManager.getZKConfig().getPath() + "/task/" + job.getName(), jo.toString().getBytes(), CreateMode.PERSISTENT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
