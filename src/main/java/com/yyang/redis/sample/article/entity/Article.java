package com.yyang.redis.sample.article.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yyang.redis.sample.article.tool.RandomTool;

public class Article {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(Article.class);
	
	public static final String redisName = "article:";
	
	private Long id;
	
	private String title;
	
	private String link;
	
	private User poster;
	
	private Long time;
	
	private Long votes;
	
	private Map<String, String> map = new HashMap<String, String>();
	
	public Article() {
		this.id = RandomTool.getId6();
	}
	
	public Article(long id) {
		this.id = id;
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public Article(String articleId, Map map) {
		Pattern pattern = Pattern.compile("^article:(\\d+)$");
		Matcher matcher = pattern.matcher(articleId);
		if(!matcher.matches()) {
			LOGGER.error("This {} not match the article's Pattern, So can't make it to a Article.", articleId);
			return;
		}
		
		this.id = Long.parseLong(matcher.group(1));
		this.map = map;
		fillPropertiesWitMap();
	}
	
	private void fillPropertiesWitMap() {
		this.link = this.map.get("link");
		this.poster = new User(Long.parseLong(this.map.get("poster").split(":")[1]));
		this.time = Long.parseLong(this.map.get("time"));
		this.title = this.map.get("title");
		this.votes = Long.parseLong(this.map.get("votes"));
	}
	
	public Long getId() {
		return this.id;
	}
	
	public String getRedisName() {
		
		return redisName + this.id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		this.map.put("title", title);
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
		this.map.put("link", link);
	}

	public User getPoster() {
		return poster;
	}

	public void setPoster(User poster) {
		this.poster = poster;
		this.map.put("poster", poster.toString());
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
		this.map.put("time", time.toString());
	}

	public Long getVotes() {
		return votes;
	}

	public void setVotes(Long votes) {
		this.votes = votes;
		this.map.put("votes", votes.toString());
	}

	public Map<String, String> getMap() {
		return map;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{")
			.append(" id:").append(id).append(",")
			.append("title:").append(title).append(",")
			.append("link:").append(link).append(",")
			.append("time:").append(time).append(",")
			.append("poster:").append(poster.toString()).append(",")
			.append("votes:").append(votes)
			.append("}");
		return sb.toString();
	}
	
}
