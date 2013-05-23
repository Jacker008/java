package com.wenwo.schedule.sdk;

import java.io.Serializable;


/**
 * @author yuxuan.wang
 * 
 */
public class ScheduleJobInfo<T extends Serializable> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6976802199769129505L;

	private String id;

	private String cron;

	private String mainClass;

	private String filePath;

	private FileType fileType;

	private transient ClassLoader classLoader;

	private boolean paused;
	
	private String desc;
	
	private String group;
	
	private String owner;

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}
	
	public ScheduleJobInfo(){
		super();
	}

	public ScheduleJobInfo(String id, String cron, String mainClass, String filePath, FileType fileType) {
		super();
		this.id = id;
		this.cron = cron;
		this.mainClass = mainClass;
		this.filePath = filePath;
		this.fileType = fileType;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public String getId() {
		return id;
	}

	public String getCron() {
		return cron;
	}

	public void setId(String jobId) {
		this.id = jobId;
	}

	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void setFileType(FileType fileType) {
		this.fileType = fileType;
	}

	public String getMainClass() {
		return mainClass;
	}

	public String getFilePath() {
		return filePath;
	}

	public FileType getFileType() {
		return fileType;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public static enum FileType {
		JAR(".jar"), ZIP(".zip");

		private String suffix;

		private FileType(String suffix) {
			this.suffix = suffix;
		}

		public String getSuffix() {
			return suffix;
		}

		/**
		 * 检测文件名类型
		 * 
		 * @param fileName
		 * @return
		 */
		public static FileType detect(String fileName) {
			for (FileType fileType : FileType.values()) {
				if (fileName.endsWith(fileType.getSuffix()))
					return fileType;
			}
			return null;
		}
	}
}
