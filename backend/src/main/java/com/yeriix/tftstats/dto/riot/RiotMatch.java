package com.yeriix.tftstats.dto.riot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RiotMatch(Metadata metadata, Info info) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Metadata(@JsonProperty("match_id") String matchId) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Info(
            @JsonProperty("game_datetime") long gameDatetime,
            @JsonProperty("game_length") double gameLength,
            @JsonProperty("queue_id") int queueId,
            @JsonProperty("tft_set_number") int tftSetNumber,
            List<Participant> participants) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Participant(
            String puuid,
            int placement,
            int level,
            @JsonProperty("gold_left") int goldLeft,
            @JsonProperty("last_round") int lastRound,
            @JsonProperty("players_eliminated") int playersEliminated,
            @JsonProperty("total_damage_to_players") int totalDamageToPlayers,
            List<String> augments,
            List<Trait> traits,
            List<Unit> units) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Trait(
            String name,
            @JsonProperty("num_units") int numUnits,
            int style) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Unit(
            @JsonProperty("character_id") String characterId,
            int tier,
            int rarity,
            List<String> itemNames) {
    }
}