package com.yeriix.tftstats.dto;

import java.util.List;

public record MatchPageDto(StatsDto stats, List<MatchSummaryDto> matches, boolean hasMore) {
}