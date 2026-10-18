package com.yeriix.tftstats.dto;

public record StatsDto(int gamesAnalyzed, double averagePlacement, double top4Rate, double winRate) {
}