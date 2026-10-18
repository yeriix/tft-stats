package com.yeriix.tftstats.dto;

import java.util.List;

public record MatchDetailDto(
        String matchId,
        long gameDatetime,
        long gameLengthSeconds,
        String queue,
        int setNumber,
        List<ParticipantDto> participants) {
}