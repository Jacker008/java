package com.wenwo.schedule.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.wenwo.schedule.web.dao.UserDao;
import com.wenwo.schedule.web.lang.ResponseContent;
import com.wenwo.schedule.web.model.User;
import com.wenwo.schedule.web.model.User.Role;

@Controller
public class UserController {

	@Autowired
	private UserDao userDao;

	@RequestMapping("user/list")
	public ModelAndView list(){
		Iterator<User> it = userDao.findAll(new Sort(Direction.DESC, "createTime")).iterator();
		List<User> users = new ArrayList<User>();
		while(it.hasNext())
			users.add(it.next());
		return new ModelAndView("user/list", "users", users);
	}
	
	@RequestMapping(value = "user/create", method = RequestMethod.POST)
	public @ResponseBody ResponseContent create(User user) {
		user.setRole(Role.USER);
		user.setCreateTime(new Date());
		user.setPassword(DigestUtils.sha1Hex(user.getPassword()));
		userDao.save(user);
		return new ResponseContent(true, "添加用户成功");
	}
}
