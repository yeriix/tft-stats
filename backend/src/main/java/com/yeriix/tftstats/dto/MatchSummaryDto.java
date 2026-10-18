package com.yeriix.tftstats.dto;

public record MatchSummaryDto(String matchId, long gameDatetime, String queue, ParticipantDto player) {
}