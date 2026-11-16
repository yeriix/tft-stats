package com.yeriix.tftstats.dto;

import java.util.List;

public record UnitDto(
        String characterId,
        String name,
        int cost,
        int tier,
        SpriteDto icon,
        List<ItemDto> items) {
}