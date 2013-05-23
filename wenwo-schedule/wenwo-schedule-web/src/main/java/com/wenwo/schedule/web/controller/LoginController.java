package com.wenwo.schedule.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.wenwo.schedule.web.dao.UserDao;
import com.wenwo.schedule.web.model.User;

/**
 * @author yuxuan.wang
 * 
 */
@Controller
public class LoginController {

	@Autowired
	private UserDao userDao;

	private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

	@RequestMapping("")
	public ModelAndView index() {
		return new ModelAndView("login");
	}

	@RequestMapping("login")
	public ModelAndView login(HttpServletRequest request, User user) {
		if(user == null || user.getUsername() == null || user.getPassword() == null)
			return new ModelAndView("login");
		User logonUser = userDao.findByUsernameAndPassword(user.getUsername(), DigestUtils.sha1Hex(user.getPassword()));
		if (LOGGER.isInfoEnabled())
			LOGGER.info("User login: " + logonUser);
		if (logonUser != null){
			request.getSession().setAttribute("user", logonUser);
			return new ModelAndView("panel");
		}
		else
			return new ModelAndView("login");
	}
}
