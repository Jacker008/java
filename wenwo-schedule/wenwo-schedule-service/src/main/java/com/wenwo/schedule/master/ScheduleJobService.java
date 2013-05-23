package com.wenwo.schedule.master;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wenwo.commons.util.FileUtil;
import com.wenwo.schedule.ScheduleHandler;
import com.wenwo.schedule.sdk.CommonLib;
import com.wenwo.schedule.sdk.IScheduleJobService;
import com.wenwo.schedule.sdk.JobExecuteInfo;
import com.wenwo.schedule.sdk.ScheduleJobInfo;
import com.wenwo.schedule.sdk.ScheduleJobInfo.FileType;

/**
 * @author yuxuan.wang
 * 
 */
public class ScheduleJobService implements IScheduleJobService {

	/**
	 * 任务信息缓存
	 */
	private Map<String, ScheduleJobInfo<?>> jobs = new HashMap<String, ScheduleJobInfo<?>>();

	/**
	 * 公共包信息缓存
	 */
	private Map<String, CommonLib> commonLibs = new HashMap<String, CommonLib>();

	private CommonLibUrlClassLoader rootClassLoader;

	private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleJobService.class);

	@Autowired
	private JobPropDao jobPropDao;

	/**
	 * 从磁盘初始化任务
	 * 
	 * @return
	 */
	@PostConstruct
	private void init() {
		// Init common libs
		initLibs();

		// Init jobs
		initJobs();
	}

	/**
	 * 初始化依赖包
	 */
	private void initLibs() {
		File rootLibPath = new File(libPath);
		if (!rootLibPath.exists())
			rootLibPath.mkdirs();
		List<URL> jarUrls = new ArrayList<URL>();
		for (File lib : rootLibPath.listFiles()) {
			String name = lib.getName();
			CommonLib clib = new CommonLib(name, new Date(lib.lastModified()));
			clib.setSize(lib.length());
			commonLibs.put(name, clib);

			try {
				jarUrls.add(createJarURL(lib.getAbsoluteFile()));
			} catch (MalformedURLException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		rootClassLoader = new CommonLibUrlClassLoader(jarUrls.toArray(new URL[] {}));
	}

	/**
	 * 初始化任务
	 */
	private void initJobs() {
		File rootPath = new File(jobPath);
		if (!rootPath.exists()) {
			rootPath.mkdirs();
		}
		for (File jobDir : rootPath.listFiles()) {
			if (LOGGER.isInfoEnabled())
				LOGGER.info("Loading job : " + jobDir.getName());

			ScheduleJobInfo<?> job = jobPropDao.getJobInfo(jobDir.getAbsolutePath());
			try {
				ClassLoader classLoader = createClassLoader(new File(job.getFilePath()), job.getFileType());
				job.setClassLoader(classLoader);
				jobs.put(job.getId(), job);
				scheduleHandler.schedule(job);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public Class<?> findClass(String jobId, String name) throws ClassNotFoundException {
		return jobs.get(jobId).getClassLoader().loadClass(name);
	}

	@Override
	public byte[] findResource(String jobId, String name) throws IOException {
		if (!name.startsWith("/")) {
			name = "/" + name.replace('.', '/') + ".class";
		}
		InputStream is = jobs.get(jobId).getClassLoader().getResourceAsStream(name);
		if (is == null)
			return null;
		else
			return IOUtils.toByteArray(is);
	}

	private String jobPath;

	@Autowired
	private ScheduleHandler scheduleHandler;

	@Override
	public <T extends Serializable> void uploadJob(ScheduleJobInfo<?> job, byte[] fileData) {
		job.setId(UUID.randomUUID().toString());
		String jobId = job.getId();
		String mainClassName = job.getMainClass();
		FileType fileType = job.getFileType();

		String specialPath = FileUtil.joinPath(jobPath, jobId);
		try {
			File filePath = saveJobToDisk(mainClassName, fileData, fileType, specialPath);
			ClassLoader classLoader = createClassLoader(filePath, fileType);
			try {
				classLoader.loadClass(mainClassName);
			} catch (ClassNotFoundException e) {
				LOGGER.error("Master无法初始化main class.", e);
			}
			job.setFilePath(filePath.getAbsolutePath());

			job.setClassLoader(classLoader);
			jobs.put(jobId, job);
			jobPropDao.saveJobInfo(job);
			scheduleHandler.schedule(job);

			if (LOGGER.isInfoEnabled())
				LOGGER.info(String.format("Job [%s] has been created.", jobId));
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}

	}

	/**
	 * 将任务存到磁盘
	 * 
	 * @param mainClassName
	 * @param fileData
	 * @param fileType
	 * @param specialPath
	 * @return
	 * @throws IOException
	 */
	private File saveJobToDisk(String mainClassName, byte[] fileData, FileType fileType, String specialPath) throws IOException {
		File jobDir = new File(specialPath);
		FileUtils.forceMkdir(jobDir);
		File filePath = new File(FileUtil.joinPath(specialPath, "job" + fileType.getSuffix()));
		FileUtils.writeByteArrayToFile(filePath, fileData);

		if (FileType.ZIP == fileType) {
			FileUtil.unzip(filePath, specialPath);
			for (File child : jobDir.listFiles()) {
				if (child.isDirectory()) {
					filePath = child;
					break;
				}
			}
		}
		return filePath.getAbsoluteFile();
	}

	/**
	 * 创建classloader
	 * 
	 * @param jobFile
	 * @param fileType
	 * @return
	 * @throws IOException
	 */
	private URLClassLoader createClassLoader(File jobFile, FileType fileType) throws IOException {
		if (FileType.JAR == fileType) {
			URL jarUrl = createJarURL(jobFile);
			return new URLClassLoader(new URL[] { jarUrl }, rootClassLoader);
		} else if (FileType.ZIP == fileType) {
			File[] children = jobFile.listFiles();
			ArrayList<URL> urlList = new ArrayList<URL>();
			for (File child : children) {
				if (child.isDirectory()) {
					if (child.getName().equals("lib")) {
						File[] libFiles = child.listFiles();
						for (File lib : libFiles) {
							urlList.add(createJarURL(lib));
						}
					} else {
						urlList.add(new URL("file:" + child.getAbsolutePath()));
					}

				} else if (child.getName().endsWith(".jar")) {
					urlList.add(createJarURL(child));
				}
			}
			return new URLClassLoader(urlList.toArray(new URL[] {}), rootClassLoader);
		}
		return null;
	}

	private URL createJarURL(File jar) throws MalformedURLException {
		return new URL("jar:file:" + jar.getAbsolutePath() + "!/");
	}

	public void setJobPath(String jobPath) {
		this.jobPath = jobPath;
	}

	@Override
	public void removeJob(String jobId) {
		// 移除调度
		scheduleHandler.remove(jobId);

		// 删除任务缓存
		ScheduleJobInfo<?> jobInfo = jobs.remove(jobId);
		if (jobInfo != null) {
			ClassLoader classLoader = jobInfo.getClassLoader();
			if (classLoader != null && classLoader instanceof URLClassLoader) {
				try {
					((URLClassLoader) classLoader).close();
				} catch (IOException e) {
				}
			}
		}

		// 删除磁盘文件
		removeDiskFile(jobId);
	}

	private void removeDiskFile(String jobId) {
		File filePath = new File(jobPath, jobId);
		try {
			FileUtils.deleteDirectory(filePath);
			if (LOGGER.isInfoEnabled())
				LOGGER.info(String.format("Job [%s] has been removed.", jobId));
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Override
	public void pauseJob(String jobId) {
		scheduleHandler.pause(jobId);
		jobs.get(jobId).setPaused(true);

		jobPropDao.setPaused(FileUtil.joinPath(jobPath, jobId), true);

		if (LOGGER.isInfoEnabled())
			LOGGER.info(String.format("Job [%s] has been paused.", jobId));
	}

	@Override
	public void resumeJob(String jobId) {
		scheduleHandler.resume(jobId);
		jobs.get(jobId).setPaused(false);

		jobPropDao.setPaused(FileUtil.joinPath(jobPath, jobId), false);

		if (LOGGER.isInfoEnabled())
			LOGGER.info(String.format("Job [%s] has been resumed.", jobId));
	}

	@Override
	public JobExecuteInfo jobInfo(String jobId) {
		return scheduleHandler.info(jobId);
	}

	@Override
	public void updateJob(String jobId, String desc, String group, String mainClassName, String cron, byte[] fileData, FileType fileType) {
		scheduleHandler.remove(jobId);
		ScheduleJobInfo<?> scheduleJobInfo = jobs.get(jobId);
		if (desc != null) {
			scheduleJobInfo.setDesc(desc);
		}
		if (fileData != null) {
			try {
				scheduleJobInfo.setId(UUID.randomUUID().toString());
				
				ClassLoader oldClassLoader = scheduleJobInfo.getClassLoader();
				((URLClassLoader) oldClassLoader).close();
				
				File filePath = saveJobToDisk(mainClassName, fileData, fileType, FileUtil.joinPath(jobPath, scheduleJobInfo.getId()));
				String jobFile = filePath.getAbsolutePath();
				scheduleJobInfo.setFilePath(jobFile);
				scheduleJobInfo.setFileType(fileType);
				scheduleJobInfo.setMainClass(mainClassName);
				ClassLoader classLoader = createClassLoader(new File(jobFile), fileType);
				scheduleJobInfo.setClassLoader(classLoader);
				
				jobs.remove(jobId);
				removeDiskFile(jobId);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		if (cron != null) {
			scheduleJobInfo.setCron(cron);
		}
		jobPropDao.saveJobInfo(scheduleJobInfo);
		jobs.put(scheduleJobInfo.getId(), scheduleJobInfo);
		scheduleHandler.schedule(scheduleJobInfo);
	}

	@Override
	public List<ScheduleJobInfo<?>> all() {
		return new ArrayList<ScheduleJobInfo<?>>(jobs.values());
	}

	@Override
	public ScheduleJobInfo<?> find(String jobId) {
		return jobs.get(jobId);
	}

	@Override
	public List<ScheduleJobInfo<?>> owner(String owner) {
		List<ScheduleJobInfo<?>> ownerJobs = new ArrayList<ScheduleJobInfo<?>>();
		for (ScheduleJobInfo<?> job : jobs.values()) {
			if (job.getOwner().equals(owner)) {
				ownerJobs.add(job);
			}
		}
		return ownerJobs;
	}

	private String libPath;

	public void setLibPath(String libPath) {
		this.libPath = libPath;
	}

	@Override
	public List<CommonLib> libs() {
		return new ArrayList<CommonLib>(commonLibs.values());
	}

	@Override
	public void uploadLib(byte[] libData, String name) {
		File newFile = new File(libPath, name);
		try {
			FileUtils.writeByteArrayToFile(newFile, libData);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		CommonLib clib = new CommonLib(name, new Date());
		clib.setSize(newFile.length());
		commonLibs.put(name, clib);
		try {
			rootClassLoader.addURL(createJarURL(newFile));
		} catch (MalformedURLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Override
	public void removeLib(String name) {
		File removeFile = new File(libPath, name);
		if (removeFile.exists())
			removeFile.delete();
		commonLibs.remove(name);
	}

	@Override
	public void triggerJob(String jobId) {
		scheduleHandler.triggerJob(jobId);
		LOGGER.info(String.format("Job[%s] has been triggered manually.", jobId));
	}

}
