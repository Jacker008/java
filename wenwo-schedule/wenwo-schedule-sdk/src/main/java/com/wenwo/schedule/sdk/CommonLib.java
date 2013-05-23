package com.wenwo.schedule.sdk;

import java.io.Serializable;
import java.util.Date;

/**
 * 公共包
 * 
 * @author yuxuan.wang
 * 
 */
public class CommonLib implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1918791348130755321L;
	private String name;
	private Date modifyTime;
	private long size;

	public CommonLib(String name, Date modifyTime) {
		super();
		this.name = name;
		this.modifyTime = modifyTime;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

}
