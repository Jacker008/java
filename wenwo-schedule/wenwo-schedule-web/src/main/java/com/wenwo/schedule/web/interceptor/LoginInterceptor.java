package com.wenwo.schedule.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.wenwo.schedule.web.model.User;

public class LoginInterceptor extends HandlerInterceptorAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		User user = (User) request.getSession().getAttribute("user");
		if (user == null) {
			LOGGER.warn("Not login, Go away!");
			String path = "/" + request.getContextPath();
			response.sendRedirect(path);
			return false;
		}
		return super.preHandle(request, response, handler);
	}
}
