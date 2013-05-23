package com.wenwo.schedule.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 负责接收用户自定义上传的调度任务，并将任务分片传入MQ
 * 
 * @author yuxuan.wang
 * 
 */
public class WenwoScheduleMaster {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Logger logger = LoggerFactory.getLogger(WenwoScheduleMaster.class);

		try {
			new ClassPathXmlApplicationContext(new String[] { "classpath:/schedule-master.xml" }).registerShutdownHook();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}
	}

}
