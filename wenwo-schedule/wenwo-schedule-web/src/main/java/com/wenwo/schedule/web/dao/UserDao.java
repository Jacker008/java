package com.wenwo.schedule.web.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.wenwo.schedule.web.model.User;

public interface UserDao extends PagingAndSortingRepository<User, String> {
	
	User findByUsernameAndPassword(String username, String password);

}
