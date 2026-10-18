package com.yeriix.tftstats.dto;

import java.util.List;

public record UnitDto(String characterId, int tier, int rarity, List<String> items) {
}