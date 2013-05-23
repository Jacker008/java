package com.wenwo.schedule.sdk;

import java.io.Serializable;
import java.util.Date;

/**
 * 任务运行信息
 * 
 * @author yuxuan.wang
 * 
 */
public class JobExecuteInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3141459817300855252L;

	private Date startTime;
	private Date previousFireTime;
	private Date nextFireTime;

	public JobExecuteInfo(Date startTime, Date previousFireTime, Date nextFireTime) {
		super();
		this.startTime = startTime;
		this.previousFireTime = previousFireTime;
		this.nextFireTime = nextFireTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getPreviousFireTime() {
		return previousFireTime;
	}

	public Date getNextFireTime() {
		return nextFireTime;
	}

}
