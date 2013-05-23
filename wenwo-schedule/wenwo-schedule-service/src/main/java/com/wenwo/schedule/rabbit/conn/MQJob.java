package com.wenwo.schedule.rabbit.conn;

import java.io.Serializable;

/**
 * @author yuxuan.wang
 * 
 */
public class MQJob implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2874606566065054047L;

	private byte[] classData;
	private String classLoader;
	private String owner;

	public MQJob() {
		super();
	}

	public MQJob(byte[] classData, String classLoader, String owner) {
		super();
		this.classData = classData;
		this.classLoader = classLoader;
		this.owner = owner;
	}

	public byte[] getClassData() {
		return classData;
	}

	public void setClassData(byte[] classData) {
		this.classData = classData;
	}

	public String getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(String classLoader) {
		this.classLoader = classLoader;
	}

	public String getOwner() {
		return owner;
	}
}
