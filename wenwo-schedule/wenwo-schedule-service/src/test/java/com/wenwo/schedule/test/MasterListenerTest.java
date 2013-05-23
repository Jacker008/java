package com.wenwo.schedule.test;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.missian.client.sync.SyncMissianProxyFactory;
import com.wenwo.schedule.sdk.IScheduleJobService;

public class MasterListenerTest {

	@Test
	public void testUploadJob() throws IOException {
		SyncMissianProxyFactory syncMissian = new SyncMissianProxyFactory();
		IScheduleJobService masterListener = (IScheduleJobService) syncMissian.create(IScheduleJobService.class,
				String.format("tcp://%s:%s/%s", "localhost", 18888, "masterListener"));
		String file = "C:\\Users\\yuxuan.wang\\Desktop\\SimpleJobTest.jar";
		byte[] fileData = FileUtils.readFileToByteArray(new File(file));
		masterListener.uploadJob(null, fileData);
	}

	@Test
	public void testPauseJob() {
		SyncMissianProxyFactory syncMissian = new SyncMissianProxyFactory();
		IScheduleJobService masterListener = (IScheduleJobService) syncMissian.create(IScheduleJobService.class,
				String.format("tcp://%s:%s/%s", "localhost", 18888, "masterListener"));
		masterListener.pauseJob("TestJob");
	}

	@Test
	public void testResumeJob() {
		SyncMissianProxyFactory syncMissian = new SyncMissianProxyFactory();
		IScheduleJobService masterListener = (IScheduleJobService) syncMissian.create(IScheduleJobService.class,
				String.format("tcp://%s:%s/%s", "localhost", 18888, "masterListener"));
		masterListener.resumeJob("TestJob");
	}

	@Test
	public void testRemoveJob() {
		SyncMissianProxyFactory syncMissian = new SyncMissianProxyFactory();
		IScheduleJobService masterListener = (IScheduleJobService) syncMissian.create(IScheduleJobService.class,
				String.format("tcp://%s:%s/%s", "localhost", 18888, "masterListener"));
		masterListener.removeJob("TestJob");
	}
	
}
