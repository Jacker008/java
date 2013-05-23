package com.wenwo.schedule.web.lang;

public class ResponseContent {

	private boolean suc;
	private String msg;

	public ResponseContent(boolean suc, String msg) {
		super();
		this.suc = suc;
		this.msg = msg;
	}

	public boolean isSuc() {
		return suc;
	}

	public void setSuc(boolean suc) {
		this.suc = suc;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
