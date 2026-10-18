package com.yeriix.tftstats.dto;

import java.util.List;

public record PlayerProfileDto(
        String puuid,
        String gameName,
        String tagLine,
        RankDto ranked,
        StatsDto stats,
        List<MatchSummaryDto> matches) {
}