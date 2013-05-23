package com.wenwo.schedule.slaver;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wenwo.schedule.JobDispatcher;

/**
 * @author yuxuan.wang
 * 
 */
public class SlaverConsumer {

	private final JobDispatcher jobDispatcher;

	private int threads;

	public SlaverConsumer(JobDispatcher jobDispatcher, int threads) {
		super();
		this.jobDispatcher = jobDispatcher;
		this.threads = threads;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(SlaverConsumer.class);

	public void start() {
		if (threads <= 0) {
			throw new IllegalArgumentException("Consumer的数量不能小于等于0.");
		}

		ExecutorService executor = Executors.newFixedThreadPool(threads);
		for (int i = 0; i < threads; i++) {
			final int batch = i;
			executor.execute(new Runnable() {

				@Override
				public void run() {
					LOGGER.info("Consumer [" + batch + "] started.");
					jobDispatcher.consume();
				}
			});
		}
	}

}
