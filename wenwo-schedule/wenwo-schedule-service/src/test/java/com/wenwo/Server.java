package com.wenwo;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.wenwo.schedule.rabbit.conn.RabbitUtil;

public class Server {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Channel channel = null;
		try {
			channel = RabbitUtil.openChannel("10.8.2.189", 5673,"","", "myQueue");

			for (int i = 0; i < 10; i++) {
				String message = "你好坏ooo---" + i;
				channel.basicPublish("", "myQueue", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
				System.out.println(" [x] Sent '" + message + "'");
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			RabbitUtil.closeChannel(channel);
		}

	}

}
