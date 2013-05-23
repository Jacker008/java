package com.wenwo;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.wenwo.schedule.rabbit.conn.RabbitUtil;

public class Cient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Channel channel = null;
		try {
			channel = RabbitUtil.openChannel("10.8.2.189", 5673, "","", "myQueue");
			channel.basicQos(1);

			QueueingConsumer consumer = new QueueingConsumer(channel);
			channel.basicConsume("myQueue", false, consumer);

			while (true) {
				QueueingConsumer.Delivery delivery = consumer.nextDelivery();
				String message = new String(delivery.getBody());

				System.out.println(" [x] Received '" + message + "'");
				// doWork(message);
				System.out.println(" [x] Done");
				channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} finally {
			RabbitUtil.closeChannel(channel);
		}

	}

}
