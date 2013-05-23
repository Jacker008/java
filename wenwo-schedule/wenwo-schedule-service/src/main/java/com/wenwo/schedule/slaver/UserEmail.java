package com.wenwo.schedule.slaver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;

public class UserEmail {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public String getEmail(String owner){
		DBCollection coll = mongoTemplate.getCollection("user");
		DBObject query = QueryBuilder.start("_id").is(owner).get();
		DBObject user = coll.findOne(query);
		return (String) user.get("email");
	}

}
