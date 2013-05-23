package com.wenwo.schedule.web.model;

import java.util.Date;

import org.springframework.data.annotation.Id;

/**
 * 帐号
 * 
 * @author yuxuan.wang
 * 
 */
public class User {

	@Id
	private String username;
	private String password;
	private Role role;

	private String desc;
	
	private String email;
	
	private Date createTime;

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public enum Role {
		ADMIN, USER;
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", role=" + role + "]";
	}

}
