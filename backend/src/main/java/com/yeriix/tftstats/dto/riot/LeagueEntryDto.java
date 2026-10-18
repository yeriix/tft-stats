package com.yeriix.tftstats.dto.riot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LeagueEntryDto(String queueType, String tier, String rank, Integer leaguePoints) {
}