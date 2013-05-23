package com.wenwo.schedule.rabbit.conn;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Rabbit connection utility
 * 
 * @author yuxuan.wang
 * 
 */
public final class RabbitUtil {

	/**
	 * Open a channel
	 * @param host
	 * @param port
	 * @param username
	 * @param password
	 * @param queue
	 * @return
	 * @throws IOException
	 */
	public static Channel openChannel(String host, int port, String username, String password, String queue) throws IOException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host);
		factory.setPort(port);
		factory.setUsername(username);
		factory.setPassword(password);

		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(queue, true, false, false, null);

		return channel;
	}

	/**
	 * Close a channel and the connection which carries it
	 * 
	 * @param channel
	 */
	public static void closeChannel(Channel channel) {
		if (channel != null) {
			Connection conn = channel.getConnection();
			try {
				channel.close();
			} catch (IOException e) {
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (IOException e) {
				}
			}
		}
	}

}
