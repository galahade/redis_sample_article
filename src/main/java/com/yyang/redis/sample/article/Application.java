package com.yyang.redis.sample.article;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.yyang.redis.sample.article.entity.Article;
import com.yyang.redis.sample.article.entity.Group;
import com.yyang.redis.sample.article.service.ArticleService;
import com.yyang.redis.sample.article.tool.ArticleTool;

@SpringApplicationConfiguration
public class Application {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
	private static final int TOTAL_NUMBER = 10000;
	
	@Bean
	JedisConnectionFactory connectionFactory() {
		JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
		connectionFactory.setHostName("192.168.56.110");
		return connectionFactory;
	}
	
	@Bean
	StringRedisTemplate template(RedisConnectionFactory connectionFactory) {
		return new StringRedisTemplate(connectionFactory);
	}
	
	@Bean
	ArticleService articleService() {
		return new ArticleService();
	}
	
	@Bean
	RandomStringUtils randomStringUtils() {
		return new RandomStringUtils();
	}
	
	public static void main(String[] args) {
		
		ConfigurableApplicationContext cxt = SpringApplication.run(Application.class, args);
		ArticleService articleService = cxt.getBean(ArticleService.class);
		
		invokeArticleService(articleService);
	}
	
	private static void invokeArticleService(ArticleService articleService) {
		//articleService.getArticles(1, "time:");
		//
		//addBulkArticles(articleService);
		articleService.getGroupArticles(Group.Programming, 1, "score:");
	}

	private static void addBulkArticles(ArticleService articleService) {
		Long start = System.currentTimeMillis();
		
		for(int i = 0; i < TOTAL_NUMBER; i++) {
			addArticle(articleService);
		}
		Long end = System.currentTimeMillis();
		LOGGER.error("Total {} records execute on {} milli seconds", TOTAL_NUMBER, (end - start));
	}
	
	private static void addArticle(ArticleService articleService) {
		
		Article article = articleService.postArticle();
		articleService.articleVote(article.getPoster(), article);
		List<Group> groups = new ArrayList<Group>();
		groups.add(ArticleTool.getRandomGroup());
		articleService.addGroups(article, groups);
	}
	

}
