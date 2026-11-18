package com.yeriix.tftstats.dto;

public record PlayerProfileDto(
        String puuid,
        String gameName,
        String tagLine,
        RankDto ranked,
        String profileIconUrl,
        long summonerLevel) {
}