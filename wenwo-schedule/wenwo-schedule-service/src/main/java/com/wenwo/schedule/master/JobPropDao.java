package com.wenwo.schedule.master;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wenwo.commons.util.FileUtil;
import com.wenwo.schedule.sdk.ScheduleJobInfo;
import com.wenwo.schedule.sdk.ScheduleJobInfo.FileType;

/**
 * 任务属性的持久化
 * 
 * @author yuxuan.wang
 * 
 */
public class JobPropDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(JobPropDao.class);

	private static final String JOB_PROPERTIES_NAME = "job.properties";

	/**
	 * Save schedule job info to disk
	 * 
	 * @param jobInfo
	 */
	public void saveJobInfo(ScheduleJobInfo<?> jobInfo) {
		String filePath = jobInfo.getFilePath();
		Properties props = new Properties();
		props.setProperty("jobId", jobInfo.getId());
		props.setProperty("cron", jobInfo.getCron());
		props.setProperty("mainClass", jobInfo.getMainClass());
		props.setProperty("fileType", jobInfo.getFileType().name());
		props.setProperty("isPaused", "false");
		props.setProperty("filePath", jobInfo.getFilePath());
		props.setProperty("desc", jobInfo.getDesc());
		props.setProperty("group", jobInfo.getGroup());
		props.setProperty("owner", jobInfo.getOwner());
		try {
			File propertyFile = new File(new File(filePath).getParent(), JOB_PROPERTIES_NAME);
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(propertyFile);
				props.store(out, null);
			} finally {
				if (out != null)
					out.close();
			}
		} catch (FileNotFoundException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public <T extends Serializable> ScheduleJobInfo<T> getJobInfo(String jobDir) {
		ScheduleJobInfo<T> job = null;
		File propFile = new File(jobDir, JOB_PROPERTIES_NAME);
		if (!propFile.exists()) {
			LOGGER.warn("Property file for job [%s] do not exists.");
			return null;
		}
		Properties props = new Properties();
		InputStream in = null;
		try {
			in = new FileInputStream(propFile);
			props.load(in);
			String jobId = props.getProperty("jobId");
			FileType fileType = FileType.valueOf(props.getProperty("fileType"));
			String mainClassName = props.getProperty("mainClass");
			boolean isPaused = Boolean.valueOf(props.getProperty("isPaused"));
			String cron = props.getProperty("cron");
			String filePath = props.getProperty("filePath");
			String desc = props.getProperty("desc");
			String owner = props.getProperty("owner");
			String group = props.getProperty("group");

			job = new ScheduleJobInfo<T>(jobId, cron, mainClassName, filePath, fileType);
			job.setPaused(isPaused);
			job.setDesc(desc);
			job.setOwner(owner);
			job.setGroup(group);
			if (LOGGER.isInfoEnabled())
				LOGGER.info(String.format("Job [%s] has been initialized.", jobId));
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
				}
		}
		return job;
	}

	/**
	 * 记录任务的暂停/启动信息
	 * 
	 * @param jobId
	 * @param isPaused
	 */
	public void setPaused(String jobDir, boolean isPaused) {
		String propFile = FileUtil.joinPath(jobDir, JOB_PROPERTIES_NAME);
		Properties props = new Properties();
		try {
			FileInputStream in = null;
			try {
				in = new FileInputStream(propFile);
				props.load(in);
			} finally {
				if (in != null)
					in.close();
			}
			props.setProperty("isPaused", Boolean.toString(isPaused));
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(propFile);
				props.store(out, null);
			} finally {
				if (out != null)
					out.close();
			}
		} catch (FileNotFoundException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

}
