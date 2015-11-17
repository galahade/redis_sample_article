package com.yyang.redis.sample.article.tool;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomTool {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RandomTool.class);
	
	private static final long six = 100000;
	
	public static final Long getId6() {
		Random random = new Random();
		Long value = six + (long)(random.nextDouble() * 10000);
		LOGGER.debug("Random 6 int value is :" + value);
		return value;
	}
	
}
