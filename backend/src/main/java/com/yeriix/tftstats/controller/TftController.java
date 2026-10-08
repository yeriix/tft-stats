package com.yeriix.tftstats.controller;

import tools.jackson.databind.JsonNode;
import com.yeriix.tftstats.service.TftService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tft")
public class TftController {

    private final TftService tftService;

    public TftController(TftService tftService) {
        this.tftService = tftService;
    }

    @GetMapping("/players/{gameName}/{tagLine}/matches")
    public List<JsonNode> getMatches(@PathVariable String gameName,
                                     @PathVariable String tagLine,
                                     @RequestParam(defaultValue = "5") int count) {
        return tftService.getRecentMatches(gameName, tagLine, count);
    }
}