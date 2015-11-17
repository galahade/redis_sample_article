package com.yyang.redis.sample.article.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.yyang.redis.sample.article.entity.Article;
import com.yyang.redis.sample.article.entity.Group;
import com.yyang.redis.sample.article.entity.Time;
import com.yyang.redis.sample.article.entity.User;
import com.yyang.redis.sample.article.tool.ArticleTool;
@Service
public class ArticleService {

	@Autowired
	private StringRedisTemplate redisTemplate;
	
	public static final long ONE_WEEK_IN_SECONDS = 7 * 86400;
	public static final int VOTE_SCORE = 432;
	public static final int ARTICLES_PER_PAGE = 25;
	public static final String VOTED_TITLE = "voted:";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ArticleService.class);

	
	public void articleVote(User user, Article article) {
		Date now = new Date();
		Long cutoff = now.getTime() - ONE_WEEK_IN_SECONDS;
		Double score = redisTemplate.opsForZSet().score(Time.title, article.getRedisName());
		LOGGER.debug("aritcle {}'s time score is: {}",article.getId(), score);
		if (score != null && score < cutoff)
			return;
		
		Long articleId = article.getId();
		LOGGER.debug("Current article id is {}", articleId);
		Long votedId = redisTemplate.opsForSet().add(VOTED_TITLE + articleId.toString(), user.toString());
		LOGGER.debug("User {} voted article {} on {}", user.toString(), articleId, votedId);
		if(votedId != null) {
			redisTemplate.opsForZSet().incrementScore("score:", article.getRedisName(), VOTE_SCORE);
			redisTemplate.opsForHash().increment(article.getRedisName(), "votes", 1);
		}
	}
	
	public Article postArticle() {
		Long articleId = redisTemplate.opsForValue().increment("article:", 1);
		LOGGER.debug("New article id is : {}", articleId);
		Article article = new Article(articleId);
		ArticleTool.fillRandomContent(article);
		String voted = VOTED_TITLE + articleId;
		
		redisTemplate.opsForSet().add(voted, article.getPoster().toString());
		redisTemplate.expire(voted, ONE_WEEK_IN_SECONDS, TimeUnit.SECONDS);
		LOGGER.info("Redis SET add. SET {} add a new item {}" , voted, article.getPoster().toString());

		redisTemplate.opsForHash().putAll(article.getRedisName(), article.getMap());
		LOGGER.info("Redis add HASH. create a new HASH {} " , article.getRedisName());

		redisTemplate.opsForZSet().add("score:", article.getRedisName(), article.getTime() + VOTE_SCORE);
		LOGGER.info("Redis ZSET add. ZSET score: add a new item {}", article.getRedisName());

		redisTemplate.opsForZSet().add(Time.title, article.getRedisName(), article.getTime());
		LOGGER.info("Redis ZSET add. ZSET {} add a new item {}" ,Time.title, article.getRedisName());

		return article;
	}
	
	public void addGroups(Article article, List<Group> groups) {
		for(Group group : groups) {
			redisTemplate.opsForSet().add("group:" + group.name(), article.getRedisName());
			LOGGER.info("Redis SET add. SET group:{} add a new item {}", group.name(), article.getRedisName());
		}
	}
	
	public List<Article> getGroupArticles(Group group,int page,String order) {
		String key = order + group.name();
		if(!redisTemplate.hasKey(key)) {
			redisTemplate.opsForZSet().intersectAndStore(order, "group:"+group.name(), key);
			LOGGER.info("Redis ZSET create. Create a new ZSET named : {}. InterStore with SET {} and ZSET {}", key, "group:"+group.name(), order);
		}
		redisTemplate.expire(key, 60, TimeUnit.SECONDS);
		return getArticles(page, key);
	}
	
	public List<Article> getArticles(int page, String order) {
		LOGGER.debug("Start to get Articles ----------------------------------");
		int start = (page-1) * ARTICLES_PER_PAGE;
		int end = start + ARTICLES_PER_PAGE;
		List<Article> articles = new ArrayList<Article>(ARTICLES_PER_PAGE);
		Set<String> articleIds = redisTemplate.opsForZSet().reverseRange(order, start, end);
		for(String articleId : articleIds) {
			Map<Object, Object> articleMap = redisTemplate.opsForHash().entries(articleId);
			Article article = new Article(articleId, articleMap);
			LOGGER.debug("Article {}'s details is:{}", articleId, article.toString());
			articles.add(article);
		}
		LOGGER.debug("End to get Articles ----------------------------------");
		return articles;
	}
	
	protected Long postArticle(User user, String title, URL link) {
		Long articleId = redisTemplate.opsForValue().increment("article:", 1);
		String voted = VOTED_TITLE + articleId;
		redisTemplate.opsForSet().add(voted, user.toString());
		redisTemplate.expire(voted, ONE_WEEK_IN_SECONDS, TimeUnit.SECONDS);
		Date now = new Date();
		Article article = new Article(articleId);
		ArticleTool.fillRandomContent(article);
		redisTemplate.opsForHash().putAll(article.getRedisName(), article.getMap());
		redisTemplate.opsForZSet().add("score:", article.getRedisName(), now.getTime() + VOTE_SCORE);
		redisTemplate.opsForZSet().add(Time.title, article.getRedisName(), now.getTime());
		LOGGER.info("Add a new article HASH : {} to Redis" + articleId);
		return articleId;
	}
}
