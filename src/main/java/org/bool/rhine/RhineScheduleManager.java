package org.bool.rhine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.bool.rhine.task.RhineQuartzJob;
import org.bool.rhine.zookeeper.ZKManager;
import org.bool.rhine.zookeeper.ZKUtility;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import net.sf.json.JSONObject;

/**
 *
 *
 * Author: 不二   
 *
 * Copyright @ 2018
 * 
 */
public class RhineScheduleManager {
	
	private static Scheduler scheduler = null;
	
	private static TriggerBuilder<Trigger> triggerBuilder = null;
	
	private static Lock lock = new ReentrantLock();
	
	private static final String JOB_GROUP_NAME = "RHINE_JOB_GROUP";
	
	private static final String TRIGGER_GROUP_NAME = "RHINE_TRIGGER_GROUP";
	
	private static final String TRIGGER_NAME = "RHINE_TRIGGER_NAME";
	
	private static Scheduler getScheduler() {
		try {
			return scheduler == null ? (scheduler = new StdSchedulerFactory().getScheduler()) : scheduler;
		} catch (SchedulerException e) {
			return scheduler;
		}
	}
	
	private static TriggerBuilder<Trigger> getTriggerBuilder() {
		if (triggerBuilder == null) {
			triggerBuilder = TriggerBuilder.newTrigger();
			triggerBuilder.withIdentity(TRIGGER_NAME, TRIGGER_GROUP_NAME);
		}
		return triggerBuilder;
	}
	
	/**
	 * reload 所有任务和策略
	 */
	public static void loadStrategyAndTask() {
		//0.清除已有的任务
		clearAllTasks();
		//1.连ZK
		ZKUtility.connectForever();
		try {
			lock.lock();
			//2.加载任务&策略&节点，并按策略执行任务
			//2.0加载节点信息
			List<String> nodes = ZKUtility.getChildren(ZKManager.getZKConfig().getPath() + "/node", true);
			if (!isLeader(nodes)) {
				return;
			}
			//2.1加载任务
			HashMap<String, String> jobMap = new HashMap<String, String>();
			String taskPath = ZKManager.getZKConfig().getPath() + "/task";
			List<String> paths = ZKUtility.getChildren(taskPath, true);
			if (paths != null && paths.size() > 0) {
				for (String path : paths) {
					//{"job_name":"rhineTestJob","class_name":"org.bool.rhine.task.RhineQuartzDemoJob"}
					byte[] data = ZKUtility.getData(taskPath + "/" + path, true);
					JSONObject jo = JSONObject.fromObject(new String(data));
					String jobName = jo.getString("job_name");
					String className = jo.getString("class_name");
					jobMap.put(jobName, className);
				}
			}
			//2.2加载策略
			HashMap<String, String> strategyMap = new HashMap<String, String>();
			String strategyPath = ZKManager.getZKConfig().getPath() + "/strategy";
			List<String> strategys = ZKUtility.getChildren(strategyPath, true);
			if (strategys != null && strategys.size() > 0) {
				for (String strategy : strategys) {
					//{"createTime":"","crontab":"0/5 * * * * ?","strategyName":"rhineTestJobStrategy","taskName":"rhineTestJob"}
					byte[] data = ZKUtility.getData(strategyPath + "/" + strategy, true);
					JSONObject jo = JSONObject.fromObject(new String(data));
					String jobName = jo.getString("taskName");
					String crontab = jo.getString("crontab");
					strategyMap.put(jobName, crontab);
				}
			}
			//3.重新初始化本地任务
			Iterator<String> keys = strategyMap.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				String crontab = strategyMap.get(key);
				String className = jobMap.get(key);
				addTask(className, crontab, key);
			}
			if (!getScheduler().isShutdown()) {				
				getScheduler().start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
	/**
	 * 判读当前的节点是否为leader节点
	 * @param nodes
	 * @return
	 */
	private static boolean isLeader(List<String> nodes) {
		if (nodes == null || nodes.isEmpty()) {
			return false;
		}
		long local = 0l;
		for (int i = 0; i < nodes.size(); i++) {
			String path = nodes.get(i);
			if (path.startsWith(RhineNodeManager.getNodeName())) {
				local = getSequence(path);
				break;
			}
		}
		for (String path : nodes) {
			if (local > getSequence(path)) {
				return false;
			}
		}
		return true;
	}

	private static long getSequence(String path) {
		String[] dirs = path.split("/");
		String[] nodes = dirs[dirs.length-1].split("#");
		String sequence = nodes[nodes.length - 1];
		return Long.parseLong(sequence);
	}

	/**
	 * 添加一个任务
	 * @param className
	 * @param crontab
	 * @throws ClassNotFoundException 
	 * @throws SchedulerException 
	 */
	private static void addTask(String className, String crontab, String jobName) throws ClassNotFoundException, SchedulerException {
		@SuppressWarnings("unchecked")
		Class<RhineQuartzJob> jobClass = (Class<RhineQuartzJob>) Class.forName(className);
		JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, JOB_GROUP_NAME).build();
		getTriggerBuilder().startNow();
		getTriggerBuilder().withSchedule(CronScheduleBuilder.cronSchedule(crontab));
		CronTrigger trigger = (CronTrigger) getTriggerBuilder().build();
		getScheduler().scheduleJob(jobDetail, trigger);
	}

	/**
	 * clear 所有的任务
	 * @throws SchedulerException 
	 */
	public static void clearAllTasks() {
		try {
			getScheduler().clear();
		} catch (SchedulerException e) {}
	}
}
