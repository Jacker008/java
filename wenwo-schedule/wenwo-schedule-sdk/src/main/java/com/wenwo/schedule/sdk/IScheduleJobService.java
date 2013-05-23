package com.wenwo.schedule.sdk;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import com.wenwo.schedule.sdk.ScheduleJobInfo.FileType;

public interface IScheduleJobService {

	Class<?> findClass(String jobId, String name) throws ClassNotFoundException;

	/**
	 * 查找资源
	 * 
	 * @param jobId
	 * @param name
	 * @return
	 */
	byte[] findResource(String jobId, String name) throws IOException;

	/**
	 * 上传任务
	 * 
	 * @param job
	 *            任务信息
	 * @param fileData
	 *            任务文件
	 */
	<T extends Serializable> void uploadJob(ScheduleJobInfo<?> job, byte[] fileData);

	/**
	 * 更新任务
	 * 
	 * @param jobId
	 *            任务id
	 * @param desc
	 *            任务简介
	 * @param group
	 *            任务分组
	 * @param mainClass
	 *            任务class
	 * @param cron
	 *            调度时间
	 * @param fileData
	 *            任务文件
	 * @param fileType
	 *            任务文件类型
	 */
	void updateJob(String jobId, String desc, String group, String mainClass, String cron, byte[] fileData, FileType fileType);

	/**
	 * 删除任务
	 * 
	 * @param jobId
	 */
	void removeJob(String jobId);

	/**
	 * 暂停任务
	 * 
	 * @param jobId
	 */
	void pauseJob(String jobId);

	/**
	 * 恢复任务
	 * 
	 * @param jobId
	 */
	void resumeJob(String jobId);

	/**
	 * 手动执行任务
	 * 
	 * @param jobId
	 */
	void triggerJob(String jobId);

	/**
	 * 获取任务运行信息
	 * 
	 * @param jobId
	 * @return
	 */
	JobExecuteInfo jobInfo(String jobId);

	/**
	 * 获取所有 任务
	 * 
	 * @return
	 */
	List<ScheduleJobInfo<?>> all();

	/**
	 * 获取某人的任务
	 * 
	 * @param owner
	 * @return
	 */
	List<ScheduleJobInfo<?>> owner(String owner);

	/**
	 * 获取单个任务
	 * 
	 * @param jobId
	 * @return
	 */
	ScheduleJobInfo<?> find(String jobId);

	/**
	 * 获取公共包列表
	 * 
	 * @return
	 */
	List<CommonLib> libs();

	/**
	 * 上传公共包
	 * 
	 * @param libData
	 * @param name
	 */
	void uploadLib(byte[] libData, String name);

	/**
	 * 删除公共包
	 * 
	 * @param name
	 */
	void removeLib(String name);
}
