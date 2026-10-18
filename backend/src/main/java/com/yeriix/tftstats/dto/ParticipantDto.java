package com.yeriix.tftstats.dto;

import java.util.List;

public record ParticipantDto(
        int placement,
        int level,
        int goldLeft,
        int lastRound,
        int playersEliminated,
        int totalDamageToPlayers,
        boolean you,
        List<String> augments,
        List<TraitDto> traits,
        List<UnitDto> units) {
}