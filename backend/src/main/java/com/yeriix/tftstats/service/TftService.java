package com.yeriix.tftstats.service;

import tools.jackson.databind.JsonNode;
import com.yeriix.tftstats.client.RiotClient;
import com.yeriix.tftstats.dto.AccountDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TftService {

    private final RiotClient riotClient;

    public TftService(RiotClient riotClient) {
        this.riotClient = riotClient;
    }

    public List<JsonNode> getRecentMatches(String gameName, String tagLine, int count) {
        AccountDto account = riotClient.getAccount(gameName, tagLine);
        List<String> matchIds = riotClient.getTftMatchIds(account.puuid(), count);
        return matchIds.stream()
                .map(riotClient::getTftMatch)
                .toList();
    }
}