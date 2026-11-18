package com.yeriix.tftstats.staticdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

/** Builds summoner icon URLs from the League of Legends Data Dragon CDN. */
@Service
public class ProfileIconService {

    private static final Logger log = LoggerFactory.getLogger(ProfileIconService.class);

    private static final String VERSIONS_URL = "https://ddragon.leagueoflegends.com/api/versions.json";
    private static final String CDN_URL = "https://ddragon.leagueoflegends.com/cdn/";
    private static final String FALLBACK_VERSION = "16.22.1";

    private final RestClient restClient = RestClient.create();
    private volatile String version;

    public String iconUrl(int profileIconId) {
        return CDN_URL + latestVersion() + "/img/profileicon/" + profileIconId + ".png";
    }

    private String latestVersion() {
        String current = version;
        if (current != null) {
            return current;
        }
        try {
            List<String> versions = restClient.get()
                    .uri(VERSIONS_URL)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<String>>() {});
            if (versions != null && !versions.isEmpty()) {
                version = versions.get(0);
                return version;
            }
        } catch (RuntimeException e) {
            log.warn("Could not load the Data Dragon versions, using fallback version {}", FALLBACK_VERSION);
        }
        return FALLBACK_VERSION;
    }
}