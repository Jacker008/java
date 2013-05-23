package com.wenwo.schedule;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;

import org.apache.commons.io.input.ClassLoaderObjectInputStream;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.QueueingConsumer;
import com.wenwo.commons.util.mail.MailSmtpProfile;
import com.wenwo.commons.util.mail.MailUtil;
import com.wenwo.schedule.rabbit.conn.MQJob;
import com.wenwo.schedule.rabbit.conn.RabbitUtil;
import com.wenwo.schedule.slaver.RemoteClassLoader;
import com.wenwo.schedule.slaver.UserEmail;

/**
 * 任务分发
 * 
 * @author yuxuan.wang
 * 
 */
public class JobDispatcher {

	private static final String MQ_ENCODING = "UTF-8";

	private static final Logger LOGGER = LoggerFactory.getLogger(JobDispatcher.class);

	private String master;

	private int masterPort;

	private String mqHost;
	private int mqPort;
	private String mqUsername;
	private String mqPassword;

	private String queueName;

	@Autowired
	private Gson gson;

	private Map<String, ClassLoader> classLoaders = new HashMap<String, ClassLoader>();

	/**
	 * 发布一个任务
	 * 
	 * @param unitJob
	 */
	public void provide(UnitJob<?> unitJob) {
		Channel channel = null;
		try {
			channel = RabbitUtil.openChannel(mqHost, mqPort, mqUsername, mqPassword, queueName);
			final byte[] classData = SerializationUtils.serialize(unitJob);
			final MQJob mqJob = new MQJob(classData, unitJob.getJobId(), unitJob.getOwner());
			final byte[] msg = gson.toJson(mqJob).getBytes(MQ_ENCODING);
			channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, msg);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			RabbitUtil.closeChannel(channel);
		}
	}

	/**
	 * 循环消费任务
	 */
	public void consume() {
		Channel channel = null;
		try {
			channel = RabbitUtil.openChannel(mqHost, mqPort, mqUsername, mqPassword, queueName);
			channel.basicQos(1);

			final QueueingConsumer consumer = new QueueingConsumer(channel);
			channel.basicConsume(queueName, false, consumer);

			while (true) {
				try {
					LOGGER.info("Fetching a message.");
					consumeOneJob(channel, consumer);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			RabbitUtil.closeChannel(channel);
		}
	}

	@Autowired
	private MailSmtpProfile mailSmtpProfile;

	private void consumeOneJob(Channel channel, QueueingConsumer consumer) {
		QueueingConsumer.Delivery delivery = null;
		ByteArrayInputStream bi = null;
		ClassLoaderObjectInputStream cois = null;
		String owner = null;
		String classLoader = null;
		try {
			delivery = consumer.nextDelivery();
			final byte[] body = delivery.getBody();
			final String msg = new String(body, MQ_ENCODING);
			final MQJob job = gson.fromJson(msg, MQJob.class);
			owner = job.getOwner();

			classLoader = job.getClassLoader();
			final byte[] classData = job.getClassData();

			final ClassLoader clLoader = getClassLoader(classLoader);
			Thread.currentThread().setContextClassLoader(clLoader);
			bi = new ByteArrayInputStream(classData);
			cois = new ClassLoaderObjectInputStream(clLoader, bi);

			@SuppressWarnings("rawtypes")
			final UnitJob unitJob = (UnitJob) cois.readObject();
			unitJob.doJob();

			channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			try {
				channel.basicReject(delivery.getEnvelope().getDeliveryTag(), false);
			} catch (IOException e1) {
				LOGGER.error(e1.getMessage(), e1);
			}
			if (owner != null) {
				try {
					MailUtil.send(getEmail(owner), "任务报错[" + classLoader + "]", toString(e), mailSmtpProfile);
				} catch (MessagingException e1) {
					LOGGER.error(e1.getMessage(), e1);
				}
			}
		} finally {
			if (cois != null)
				try {
					cois.close();
				} catch (IOException e) {
				}
			if (bi != null)
				try {
					bi.close();
				} catch (IOException e) {
				}
		}
	}

	private String toString(Throwable t) {
		return ExceptionUtils.getStackTrace(t).replaceAll("at ", "<br>at ");
	}

	@Autowired
	private UserEmail userEmail;

	private String getEmail(String owner) {
		return userEmail.getEmail(owner);
	}

	private ClassLoader getClassLoader(String name) {
		ClassLoader classLoader = classLoaders.get(name);
		if (classLoader == null) {
			classLoader = new RemoteClassLoader(master, masterPort, name);
			classLoaders.put(name, classLoader);
		}
		return classLoader;
	}

	public void setMaster(String master) {
		this.master = master;
	}

	public void setMasterPort(int masterPort) {
		this.masterPort = masterPort;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public void setMqHost(String mqHost) {
		this.mqHost = mqHost;
	}

	public void setMqPort(int mqPort) {
		this.mqPort = mqPort;
	}

	public void setMqUsername(String mqUsername) {
		this.mqUsername = mqUsername;
	}

	public void setMqPassword(String mqPassword) {
		this.mqPassword = mqPassword;
	}

}
