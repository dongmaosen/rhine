package org.bool.rhine;

import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.bool.rhine.zookeeper.ZKConfig;
import org.bool.rhine.zookeeper.ZKTools;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

/**
 * 做一些JOB统计工作
 *
 * Author: 不二   
 *
 * Copyright @ 2018
 * 
 */
public class RhineJobListener implements JobListener {
	
	public static final String LISTENER_NAME = "RhineJobListener";

	public String getName() {
		return LISTENER_NAME;
	}

	public void jobToBeExecuted(JobExecutionContext context) {
		String jobName = context.getJobDetail().getKey().getName();
		try {
			String path = ZKTools.getZKConfig().getPath() + "/statistics/" + jobName;
			if (!ZKTools.exist(path)) {				
				ZKTools.createPath(path, CreateMode.PERSISTENT, ZKTools.getAcl(), false);
			}
		} catch (KeeperException e) {
		} catch (Exception e) {
		}
	}

	public void jobExecutionVetoed(JobExecutionContext context) {
		//
	}
	/**
	 * 更新3个节点的信息（任务总的运行次数，第一次运行时间，最后一次运行成功时间）
	 */
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		String jobName = context.getJobDetail().getKey().getName();
		//1.更新任务运行次数
		try {
			String timesPath = ZKTools.getZKConfig().getPath() + "/statistics/" + jobName + "/times";
			if (ZKTools.exist(timesPath)) {
				byte[] times = ZKTools.getData(timesPath, false);
				long i = Long.parseLong(new String(times)) + 1;
				ZKTools.createUpdateData(timesPath, (i + "").getBytes(), CreateMode.PERSISTENT);
			} else {
 				ZKTools.create(timesPath, "1".getBytes(), CreateMode.PERSISTENT);
			}
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
		} catch (Exception e) {
		}
		//2.如果是第一次运行，更新运行时间
		try {
			String firstRunPath = ZKTools.getZKConfig().getPath() + "/statistics/" + jobName + "/firstrun";
			if (!ZKTools.exist(firstRunPath)) {
				ZKTools.createUpdateData(firstRunPath, DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss").getBytes(), CreateMode.PERSISTENT);
			}
		} catch (KeeperException e) {
		} catch (InterruptedException e) {
		} catch (Exception e) {
		}
		//3.更新最后一次运行时间
		try {
			String lastRunPath = ZKTools.getZKConfig().getPath() + "/statistics/" + jobName + "/lastrun";
			ZKTools.createUpdateData(lastRunPath, DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss").getBytes(), CreateMode.PERSISTENT);
		} catch (KeeperException e) {
		} catch (InterruptedException e) {
		} catch (Exception e) {
		}
		
	}

}
