package com.yeriix.tftstats.client;

import com.yeriix.tftstats.config.RiotProperties;
import com.yeriix.tftstats.dto.AccountDto;
import com.yeriix.tftstats.dto.riot.LeagueEntryDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class RiotClient {

    private final RestClient regionalClient;
    private final RestClient platformClient;
    private final RiotRateLimiter rateLimiter;

    public RiotClient(RiotProperties props, RiotRateLimiter rateLimiter) {
        this.regionalClient = buildClient(props.regionalHost(), props.apiKey());
        this.platformClient = buildClient(props.platformHost(), props.apiKey());
        this.rateLimiter = rateLimiter;
    }

    private static RestClient buildClient(String baseUrl, String apiKey) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-Riot-Token", apiKey)
                .build();
    }

    public AccountDto getAccount(String gameName, String tagLine) {
        rateLimiter.acquire();
        return regionalClient.get()
                .uri("/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}", gameName, tagLine)
                .retrieve()
                .body(AccountDto.class);
    }

    public List<LeagueEntryDto> getLeagueEntries(String puuid) {
        rateLimiter.acquire();
        return platformClient.get()
                .uri("/tft/league/v1/by-puuid/{puuid}", puuid)
                .retrieve()
                .body(new ParameterizedTypeReference<List<LeagueEntryDto>>() {});
    }

    public List<String> getTftMatchIds(String puuid, int count) {
        rateLimiter.acquire();
        return regionalClient.get()
                .uri("/tft/match/v1/matches/by-puuid/{puuid}/ids?count={count}", puuid, count)
                .retrieve()
                .body(new ParameterizedTypeReference<List<String>>() {});
    }

    /** Returns the match exactly as Riot sends it, so it can be stored as is. */
    public String getTftMatchRaw(String matchId) {
        rateLimiter.acquire();
        return regionalClient.get()
                .uri("/tft/match/v1/matches/{matchId}", matchId)
                .retrieve()
                .body(String.class);
    }
}