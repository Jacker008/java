package com.wenwo.schedule;

import java.io.Serializable;

import com.wenwo.schedule.sdk.SimpleJob;

/**
 * 任务执行单元
 * 
 * @author yuxuan.wang
 * 
 * @param <T>
 *            任务信息
 */
public class UnitJob<T extends Serializable> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1635603935904008343L;

	/**
	 * 任务信息
	 */
	private T jobData;

	private String jobId;
	
	private String owner;

	/**
	 * 任务执行bean
	 */
	private SimpleJob<T> jobBean;

	public UnitJob(T jobData, String jobId, SimpleJob<T> jobBean, String owner) {
		super();
		this.jobData = jobData;
		this.jobId = jobId;
		this.jobBean = jobBean;
		this.owner = owner;
	}

	public T getJobData() {
		return jobData;
	}

	public SimpleJob<T> getJobBean() {
		return jobBean;
	}

	public String getJobId() {
		return jobId;
	}

	public void doJob() throws Exception {
		jobBean.execute(jobData);
	}

	public String getOwner() {
		return owner;
	}

}
