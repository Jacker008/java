package com.wenwo.schedule;

import java.text.ParseException;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.quartz.CronTriggerBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;

import com.wenwo.schedule.sdk.JobExecuteInfo;
import com.wenwo.schedule.sdk.ScheduleJobInfo;

/**
 * @author yuxuan.wang
 * 
 */
public class ScheduleHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleHandler.class);

	private Scheduler scheduler;

	@Autowired
	private ThreadPoolTaskExecutor executor;

	@Autowired
	private JobDispatcher jobDispatcher;

	@PostConstruct
	private void init() {
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	private static final String GROUP_NAME = "jobs";

	/**
	 * 暂停任务
	 * 
	 * @param jobId
	 */
	public void pause(final String jobId) {
		try {
			scheduler.pauseJob(jobId, GROUP_NAME);
		} catch (SchedulerException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * 恢复任务
	 * 
	 * @param jobId
	 */
	public void resume(final String jobId) {
		try {
			scheduler.resumeJob(jobId, GROUP_NAME);
		} catch (SchedulerException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * 手动执行任务
	 * 
	 * @param jobId
	 */
	public void triggerJob(final String jobId) {
		try {
			scheduler.triggerJob(jobId, GROUP_NAME);
		} catch (SchedulerException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * 删除调度任务
	 * 
	 * @param jobId
	 */
	public void remove(final String jobId) {
		try {
			scheduler.deleteJob(jobId, GROUP_NAME);
		} catch (SchedulerException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * 获取任务运行信息
	 * 
	 * @param jobId
	 * @return
	 */
	public JobExecuteInfo info(final String jobId) {
		try {
			Trigger[] triggers = scheduler.getTriggersOfJob(jobId, GROUP_NAME);
			Trigger trigger = triggers[0];
			return new JobExecuteInfo(trigger.getStartTime(), trigger.getPreviousFireTime(), trigger.getNextFireTime());
		} catch (SchedulerException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 修改任务
	 * 
	 * @param jobId
	 * @param cron
	 */
	public void update(final String jobId, String cron) {
		JobDetail jobDetail;
		try {
			jobDetail = scheduler.getJobDetail(jobId, GROUP_NAME);
			Date fireTime = scheduler.rescheduleJob(jobId, GROUP_NAME, buildTrigger(jobId, cron, GROUP_NAME, jobDetail));
			LOGGER.info("更新调度时机：" + fireTime);
		} catch (SchedulerException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (ParseException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * 添加调度任务
	 * 
	 * @param jobInfo
	 */
	public void schedule(final ScheduleJobInfo<?> jobInfo) {
		try {
			JobDetail jobDetail = buildJobDetail(jobInfo, GROUP_NAME);
			CronTriggerBean triggerBean = buildTrigger(jobInfo.getId(), jobInfo.getCron(), GROUP_NAME, jobDetail);

			scheduler.deleteJob(jobInfo.getId(), GROUP_NAME);
			scheduler.scheduleJob(jobDetail, triggerBean);
			if (jobInfo.isPaused())
				scheduler.pauseJob(jobInfo.getId(), GROUP_NAME);
		} catch (ParseException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (SchedulerException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			LOGGER.error(e.getMessage(), e);
		}

	}

	/**
	 * 构造触发器
	 * 
	 * @param jobInfo
	 * @param groupName
	 * @param jobDetail
	 * @return
	 * @throws ParseException
	 */
	private CronTriggerBean buildTrigger(final String jobId, final String cron, String groupName, JobDetail jobDetail) throws ParseException {
		CronTriggerBean triggerBean = new CronTriggerBean();
		triggerBean.setName(jobId);
		triggerBean.setJobGroup(groupName);
		triggerBean.setJobName(jobId);
		triggerBean.setJobDetail(jobDetail);
		triggerBean.setCronExpression(cron);
		triggerBean.setGroup(groupName);
		return triggerBean;
	}

	/**
	 * 构造任务
	 * 
	 * @param jobInfo
	 * @param groupName
	 * @return
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 */
	private JobDetail buildJobDetail(final ScheduleJobInfo<?> jobInfo, String groupName) throws ClassNotFoundException, NoSuchMethodException {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		PrepareJob<?> job = new PrepareJob(jobInfo, jobDispatcher);
		MethodInvokingJobDetailFactoryBean jobFactory = new MethodInvokingJobDetailFactoryBean();
		jobFactory.setTargetObject(job);
		jobFactory.setTargetMethod("execute");
		jobFactory.setConcurrent(false);
		jobFactory.setName(jobInfo.getId());
		jobFactory.setGroup(groupName);
		jobFactory.afterPropertiesSet();
		JobDetail jobDetail = jobFactory.getObject();
		return jobDetail;
	}

	@PreDestroy
	private void destroy() {
		if (scheduler != null)
			try {
				scheduler.shutdown();
			} catch (SchedulerException e) {
			}
	}

}
