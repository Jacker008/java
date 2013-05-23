package com.wenwo.schedule;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wenwo.schedule.sdk.DistributedJob;
import com.wenwo.schedule.sdk.ScheduleJobInfo;
import com.wenwo.schedule.sdk.SimpleJob;

/**
 * 调度任务的封装
 * 
 * @author yuxuan.wang
 * 
 * @param <T>
 * @param <J>
 */
public class PrepareJob<T extends Serializable> {

	private ScheduleJobInfo<T> jobInfo;

	private JobDispatcher jobDispatcher;

	/**
	 * 构造准备任务
	 * 
	 */
	public PrepareJob(ScheduleJobInfo<T> jobInfo, JobDispatcher jobDispatcher) {
		super();
		this.jobInfo = jobInfo;
		this.jobDispatcher = jobDispatcher;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(PrepareJob.class);

	public void execute() {
		try {
			ClassLoader classLoader = jobInfo.getClassLoader();
			Class<?> clazz = classLoader.loadClass(jobInfo.getMainClass());
			String jobId = jobInfo.getId();

			@SuppressWarnings("unchecked")
			SimpleJob<T> jobBean = (SimpleJob<T>) clazz.newInstance();
			if (jobBean instanceof DistributedJob) {
				DistributedJob<T> slicerBean = (DistributedJob<T>) jobBean;

				List<T> sliceDatas = slicerBean.slice();
				for (T sliceData : sliceDatas) {
					UnitJob<T> unitJob = new UnitJob<T>(sliceData, jobId, jobBean, jobInfo.getOwner());
					jobDispatcher.provide(unitJob);
				}
			} else {
				jobDispatcher.provide(new UnitJob<T>(null, jobId, jobBean, jobInfo.getOwner()));
			}
		} catch (InstantiationException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

}
