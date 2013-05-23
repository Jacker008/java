package com.wenwo.schedule.web.test;

import org.apache.commons.codec.digest.DigestUtils;

public class CryptTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(DigestUtils.sha1Hex("123123"));

	}

}
