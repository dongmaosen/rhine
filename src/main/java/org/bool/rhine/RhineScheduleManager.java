package org.bool.rhine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bool.rhine.task.RhineQuartzJob;
import org.bool.rhine.zookeeper.ZKTools;
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
	
	public static Object lock = new Object();
	
	private static final String JOB_GROUP_NAME = "RHINE_JOB_GROUP";
	
	private static final String TRIGGER_GROUP_NAME = "RHINE_TRIGGER_GROUP";
		
	private static Scheduler getScheduler() {
		try {
			if (scheduler == null) {
				scheduler = new StdSchedulerFactory().getScheduler();
				scheduler.getListenerManager().addJobListener(new RhineJobListener());
			}
			return scheduler;
		} catch (SchedulerException e) {
			return scheduler;
		}
	}
	
	private static TriggerBuilder<Trigger> getTriggerBuilder() {
		if (triggerBuilder == null) {
			triggerBuilder = TriggerBuilder.newTrigger();
		}
		return triggerBuilder;
	}
	
	/**
	 * reload 所有任务和策略
	 */
	public static void loadStrategyAndTask() {
		//0.清除已有的任务
		clearAllTasks();
		try {
			//1.检查状态
			ZKTools.connectForever();
			//2.加载任务&策略&节点，并按策略执行任务
			//2.0加载节点信息
			List<String> nodes = ZKTools.getChildren(ZKTools.getZKConfig().getPath() + "/node", true);
//          单节点运行
//			if (!isLeader(nodes)) {
//				return;
//			}
			//多节点分别运行任务
			int nodeCount = nodes.size();
			int nodeSequence = 0;
			for (int i = 0; i < nodes.size(); i++) {
				if (nodes.get(i).startsWith(RhineNodeManager.getNodeName())) {
					nodeSequence = i;
					break;
				}
			}
			//2.1加载任务
			HashMap<String, String> jobMap = new HashMap<String, String>();
			String taskPath = ZKTools.getZKConfig().getPath() + "/task";
			List<String> paths = ZKTools.getChildren(taskPath, true);
			if (paths != null && paths.size() > 0) {
				for (String path : paths) {
					//{"job_name":"rhineTestJob","class_name":"org.bool.rhine.task.RhineQuartzDemoJob"}
					byte[] data = ZKTools.getData(taskPath + "/" + path, true);
					JSONObject jo = JSONObject.fromObject(new String(data));
					String jobName = jo.getString("job_name");
					String className = jo.getString("class_name");
					jobMap.put(jobName, className);
				}
			}
			//2.2加载策略
			HashMap<String, JSONObject> strategyMap = new HashMap<String, JSONObject>();
			String strategyPath = ZKTools.getZKConfig().getPath() + "/strategy";
			List<String> strategys = ZKTools.getChildren(strategyPath, true);
			if (strategys != null && strategys.size() > 0) {
				for (int i = 0; i < strategys.size(); i++) {
					//按节点顺序，取模达到负载均衡的目的
					if (i % nodeCount == nodeSequence) {						
						//{"createTime":"","crontab":"0/5 * * * * ?","strategyName":"rhineTestJobStrategy","taskName":"rhineTestJob"}
						byte[] data = ZKTools.getData(strategyPath + "/" + strategys.get(i), true);
						JSONObject jo = JSONObject.fromObject(new String(data));
						String strategyName = jo.getString("strategyName");
						strategyMap.put(strategyName, jo);
					}
				}
			}
			//3.重新初始化本地任务
			Iterator<String> keys = strategyMap.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				JSONObject jo = strategyMap.get(key);
				String taskName = jo.getString("taskName");
				if (StringUtils.isNotBlank(taskName)) {					
					addTask(jobMap.get(taskName), jo.getString("crontab"), key + "-" + jo.getString("taskName"));
				}
			}
			if (!getScheduler().isShutdown()) {				
				getScheduler().start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			runState = true;
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
			if (local < getSequence(path)) {
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
		getTriggerBuilder().withIdentity(jobName + "-trigger", TRIGGER_GROUP_NAME);
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
	/**
	 * 调度器是否正常运行
	 */
	private static boolean runState = false;
	
	public static boolean getRunState() {
		synchronized (lock) {
			return runState;			
		}
	}
	
	public static void setRunState(boolean state) {
		runState = state;
	} 
}
