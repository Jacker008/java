package com.wenwo.schedule.slaver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.missian.client.sync.SyncMissianProxyFactory;
import com.wenwo.schedule.sdk.IScheduleJobService;

public class RemoteClassLoader extends ClassLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(RemoteClassLoader.class);

	private String remoteClassLoaderName;

	private IScheduleJobService scheduleJobService;

	public RemoteClassLoader(String masterHost, int masterPort, String remoteClassLoaderName) {
		super();
		this.remoteClassLoaderName = remoteClassLoaderName;
		SyncMissianProxyFactory syncMissian = new SyncMissianProxyFactory();
		scheduleJobService = (IScheduleJobService) syncMissian.create(IScheduleJobService.class,
				String.format("tcp://%s:%s/%s", masterHost, masterPort, "scheduleJobService"), this);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		byte[] data = null;
		try {
			data = scheduleJobService.findResource(remoteClassLoaderName, name);
		} catch (IOException e) {
			LOGGER.error("加载类失败:" + name, e);
		}
		if (data == null)
			throw new ClassNotFoundException("can't load class from master: " + name);
		return this.defineClass(name, data, 0, data.length);
//		return scheduleJobService.findClass(remoteClassLoaderName, name);
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		InputStream is = super.getResourceAsStream(name);
		if (is != null) {
			return is;
		}
		byte[] bytes = null;
		try {
			bytes = scheduleJobService.findResource(remoteClassLoaderName, "/" + name);
		} catch (IOException e) {
			LOGGER.error("加载资源失败:" + name, e);
		}
		if (bytes == null)
			return null;
		return new ByteArrayInputStream(bytes);
	}

}
