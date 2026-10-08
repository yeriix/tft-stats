package com.yeriix.tftstats.client;

import tools.jackson.databind.JsonNode;
import com.yeriix.tftstats.config.RiotProperties;
import com.yeriix.tftstats.dto.AccountDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class RiotClient {

    private final RestClient restClient;

    public RiotClient(RiotProperties props) {
    this.restClient = RestClient.builder()
            .baseUrl(props.regionalHost())
            .defaultHeader("X-Riot-Token", props.apiKey())
            .build();
    }

    public AccountDto getAccount(String gameName, String tagLine) {
        return restClient.get()
                .uri("/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}", gameName, tagLine)
                .retrieve()
                .body(AccountDto.class);
    }

    public List<String> getTftMatchIds(String puuid, int count) {
        return restClient.get()
                .uri("/tft/match/v1/matches/by-puuid/{puuid}/ids?count={count}", puuid, count)
                .retrieve()
                .body(new ParameterizedTypeReference<List<String>>() {});
    }

    public JsonNode getTftMatch(String matchId) {
        return restClient.get()
                .uri("/tft/match/v1/matches/{matchId}", matchId)
                .retrieve()
                .body(JsonNode.class);
    }
}