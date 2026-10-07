package com.yeriix.tftstats.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "riot")
public record RiotProperties(String apiKey, String regionalHost) {

}