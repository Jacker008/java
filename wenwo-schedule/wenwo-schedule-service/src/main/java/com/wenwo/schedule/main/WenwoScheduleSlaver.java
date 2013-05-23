package com.wenwo.schedule.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.wenwo.schedule.slaver.SlaverConsumer;

/**
 * 调度的slaver，负责从MQ中获取消息并执行任务
 * 
 * @author yuxuan.wang
 * 
 */
public class WenwoScheduleSlaver {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Logger logger = LoggerFactory.getLogger(WenwoScheduleSlaver.class);

		try {
			@SuppressWarnings("resource")
			ClassPathXmlApplicationContext springContext = new ClassPathXmlApplicationContext(new String[] { "classpath:/schedule-slaver.xml" });
			springContext.registerShutdownHook();
			springContext.getBean(SlaverConsumer.class).start();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}
	}

}
