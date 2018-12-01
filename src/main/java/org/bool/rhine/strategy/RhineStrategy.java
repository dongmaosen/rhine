package org.bool.rhine.strategy;
/**
 *
 *
 * Author: 不二   
 *
 * Copyright @ 2018
 * 
 */
public class RhineStrategy {
	/**
	 * crontab表达式
	 */
	private String crontab;
	/**
	 * task名称
	 */
	private String taskName;
	/**
	 * strategy名称
	 */
	private String strategyName;
	/**
	 * 创建时间
	 */
	private String createTime;
	/**
	 * 描述
	 */
	private String desc;
	
	public String getCrontab() {
		return crontab;
	}
	public void setCrontab(String crontab) {
		this.crontab = crontab;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getStrategyName() {
		return strategyName;
	}
	public void setStrategyName(String strategyName) {
		this.strategyName = strategyName;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
}
