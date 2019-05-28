package org.bool.rhine.task;

import org.quartz.Job;

/**
 *
 *
 * Author: 不二   
 * 
 * Copyright @ 2018
 * 
 */
public abstract class RhineQuartzJob implements Job{
	/**
	 * 任务名称
	 */
	private String name;
	/**
	 * 任务功能描述
	 */
	private String desc;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getClassName() {
		return this.getClass().getName();
	}
}
