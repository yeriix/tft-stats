package com.yeriix.tftstats.client;

import com.yeriix.tftstats.config.RiotProperties;
import com.yeriix.tftstats.dto.AccountDto;
import com.yeriix.tftstats.dto.riot.LeagueEntryDto;
import com.yeriix.tftstats.dto.riot.RiotMatch;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RiotClient {

    private final RestClient regionalClient;
    private final RestClient platformClient;

    // Matches never change once played, so we can cache them safely.
    // In-memory only for now; it will be replaced by MySQL persistence.
    private final Map<String, RiotMatch> matchCache = new ConcurrentHashMap<>();

    public RiotClient(RiotProperties props) {
        this.regionalClient = buildClient(props.regionalHost(), props.apiKey());
        this.platformClient = buildClient(props.platformHost(), props.apiKey());
    }

    private static RestClient buildClient(String baseUrl, String apiKey) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-Riot-Token", apiKey)
                .build();
    }

    public AccountDto getAccount(String gameName, String tagLine) {
        return regionalClient.get()
                .uri("/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}", gameName, tagLine)
                .retrieve()
                .body(AccountDto.class);
    }

    public List<LeagueEntryDto> getLeagueEntries(String puuid) {
        return platformClient.get()
                .uri("/tft/league/v1/by-puuid/{puuid}", puuid)
                .retrieve()
                .body(new ParameterizedTypeReference<List<LeagueEntryDto>>() {});
    }

    public List<String> getTftMatchIds(String puuid, int count) {
        return regionalClient.get()
                .uri("/tft/match/v1/matches/by-puuid/{puuid}/ids?count={count}", puuid, count)
                .retrieve()
                .body(new ParameterizedTypeReference<List<String>>() {});
    }

    public RiotMatch getTftMatch(String matchId) {
        return matchCache.computeIfAbsent(matchId, this::fetchMatch);
    }

    private RiotMatch fetchMatch(String matchId) {
        return regionalClient.get()
                .uri("/tft/match/v1/matches/{matchId}", matchId)
                .retrieve()
                .body(RiotMatch.class);
    }
}