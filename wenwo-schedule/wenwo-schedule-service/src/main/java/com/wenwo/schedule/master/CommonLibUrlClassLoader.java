package com.wenwo.schedule.master;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author yuxuan.wang
 *
 */
public class CommonLibUrlClassLoader extends URLClassLoader {

	public CommonLibUrlClassLoader(URL[] urls) {
		super(urls);
	}

	@Override
	public void addURL(URL url) {
		super.addURL(url);
	}

}
