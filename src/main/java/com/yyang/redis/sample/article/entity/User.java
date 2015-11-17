package com.yyang.redis.sample.article.entity;

import com.yyang.redis.sample.article.tool.RandomTool;

public class User {
	
	public static final String title = "user:";
	
	private Long id;
	
	public User() {
		this.id = RandomTool.getId6();
	}
	
	public User(Long id) {
		this.id = id;
	}
	
	public Long getId() {
		return this.id;
	}
	
	@Override
	public String toString() {
		
		return title + this.id;
	}

}
