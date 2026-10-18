package com.yeriix.tftstats.controller;

import com.yeriix.tftstats.dto.MatchDetailDto;
import com.yeriix.tftstats.dto.PlayerProfileDto;
import com.yeriix.tftstats.service.TftService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tft")
public class TftController {

    private final TftService tftService;

    public TftController(TftService tftService) {
        this.tftService = tftService;
    }

    @GetMapping("/players/{gameName}/{tagLine}")
    public PlayerProfileDto getProfile(@PathVariable String gameName, @PathVariable String tagLine) {
        return tftService.getProfile(gameName, tagLine);
    }

    @GetMapping("/matches/{matchId}")
    public MatchDetailDto getMatch(@PathVariable String matchId,
                                   @RequestParam(defaultValue = "") String puuid) {
        return tftService.getMatchDetail(matchId, puuid);
    }
}