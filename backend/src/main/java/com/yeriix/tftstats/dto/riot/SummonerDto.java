package com.yeriix.tftstats.dto.riot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SummonerDto(int profileIconId, long summonerLevel) {
}