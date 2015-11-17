package com.yyang.redis.sample.article.tool;

import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.util.Assert;

import com.yyang.redis.sample.article.entity.Article;
import com.yyang.redis.sample.article.entity.Group;
import com.yyang.redis.sample.article.entity.User;

public class ArticleTool {
	
	public static final String URL = "http://www.yyang.com/article/";
	
	public static Group getRandomGroup() {
		int number = RandomUtils.nextInt(0, 4);
		return Group.values()[number];
	}
	
	public static Article fillRandomContent(final Article article) {
		Assert.notNull(article);
		article.setTitle(getRandomTitle());
		article.setLink(getRandomLink());
		article.setPoster(getRandomUser());
		article.setTime(getRandomTime());
		article.setVotes(1L);
		return article;
	}
	
	private static String getRandomTitle() {
		return RandomStringUtils.randomAscii(10);
	}
	
	private static String getRandomLink() {
		String url = URL + RandomStringUtils.randomAlphabetic(5);
		return url;
	}
	
	private static User getRandomUser() {
		return new User();
	}
	
	private static Long getRandomTime() {
		Date now = new Date();
		return now.getTime() - RandomUtils.nextLong(0, 1000000);
	}
	
	

}
