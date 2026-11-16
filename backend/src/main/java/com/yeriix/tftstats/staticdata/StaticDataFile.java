package com.yeriix.tftstats.staticdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StaticDataFile(Map<String, Entry> data) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Entry(String id, String name, Integer cost, Image image) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Image(String full, String sprite, int x, int y, int w, int h) {
    }
}