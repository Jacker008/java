package com.wenwo.schedule.sdk;

import java.io.Serializable;

/**
 * 任务执行bean
 * 
 * @author yuxuan.wang
 * 
 * @param <T>
 */
public interface SimpleJob<T extends Serializable> extends Serializable {
	void execute(T jobData) throws Exception;
}