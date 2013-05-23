package com.wenwo.schedule.web.test;

import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.wenwo.schedule.web.dao.UserDao;
import com.wenwo.schedule.web.model.User;
import com.wenwo.schedule.web.model.User.Role;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-beans.xml" })
public class UserDaoTest {

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Test
	public void testCreateUser() {
		User user = new User();
		user.setUsername("wyx");
		user.setPassword(DigestUtils.sha1Hex("28645579"));
		user.setRole(Role.ADMIN);
		user.setDesc("用于调度和监控系统的管理");
		user.setCreateTime(new Date());
		userDao.save(user);
	}
	
}
