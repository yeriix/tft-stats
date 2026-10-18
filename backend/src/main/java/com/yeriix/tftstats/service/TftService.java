package com.yeriix.tftstats.service;

import com.yeriix.tftstats.client.RiotClient;
import com.yeriix.tftstats.dto.AccountDto;
import com.yeriix.tftstats.dto.MatchDetailDto;
import com.yeriix.tftstats.dto.MatchSummaryDto;
import com.yeriix.tftstats.dto.ParticipantDto;
import com.yeriix.tftstats.dto.PlayerProfileDto;
import com.yeriix.tftstats.dto.RankDto;
import com.yeriix.tftstats.dto.StatsDto;
import com.yeriix.tftstats.dto.TraitDto;
import com.yeriix.tftstats.dto.UnitDto;
import com.yeriix.tftstats.dto.riot.RiotMatch;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class TftService {

    private static final int MATCH_COUNT = 20;
    private static final String RANKED_QUEUE = "RANKED_TFT";
    private static final String DOUBLE_UP = "Double Up";

    private final RiotClient riotClient;

    public TftService(RiotClient riotClient) {
        this.riotClient = riotClient;
    }

    public PlayerProfileDto getProfile(String gameName, String tagLine) {
        AccountDto account = riotClient.getAccount(gameName, tagLine);
        String puuid = account.puuid();

        RankDto rank = riotClient.getLeagueEntries(puuid).stream()
                .filter(entry -> RANKED_QUEUE.equals(entry.queueType()))
                .findFirst()
                .map(entry -> new RankDto(
                        entry.tier(),
                        entry.rank(),
                        entry.leaguePoints() == null ? 0 : entry.leaguePoints()))
                .orElse(null);

        List<MatchSummaryDto> matches = riotClient.getTftMatchIds(puuid, MATCH_COUNT).stream()
                .map(riotClient::getTftMatch)
                .map(match -> toSummary(match, puuid))
                .flatMap(Optional::stream)
                .toList();

        return new PlayerProfileDto(
                puuid, account.gameName(), account.tagLine(), rank, computeStats(matches), matches);
    }

    public MatchDetailDto getMatchDetail(String matchId, String puuid) {
        RiotMatch match = riotClient.getTftMatch(matchId);
        RiotMatch.Info info = match.info();

        List<ParticipantDto> participants = info.participants().stream()
                .map(p -> toParticipant(p, puuid))
                .sorted(Comparator.comparingInt(ParticipantDto::placement))
                .toList();

        return new MatchDetailDto(
                matchId,
                info.gameDatetime(),
                Math.round(info.gameLength()),
                queueName(info.queueId()),
                info.tftSetNumber(),
                participants);
    }

    private Optional<MatchSummaryDto> toSummary(RiotMatch match, String puuid) {
        return match.info().participants().stream()
                .filter(p -> puuid.equals(p.puuid()))
                .findFirst()
                .map(p -> new MatchSummaryDto(
                        match.metadata().matchId(),
                        match.info().gameDatetime(),
                        queueName(match.info().queueId()),
                        toParticipant(p, puuid)));
    }

    private ParticipantDto toParticipant(RiotMatch.Participant p, String puuid) {
        List<TraitDto> traits = nullSafe(p.traits()).stream()
                .filter(t -> t.style() > 0)
                .sorted(Comparator.comparingInt((RiotMatch.Trait t) -> t.style()).reversed())
                .map(t -> new TraitDto(t.name(), t.numUnits(), t.style()))
                .toList();

        List<UnitDto> units = nullSafe(p.units()).stream()
                .sorted(Comparator.comparingInt((RiotMatch.Unit u) -> u.tier()).reversed())
                .map(u -> new UnitDto(u.characterId(), u.tier(), u.rarity(), nullSafe(u.itemNames())))
                .toList();

        return new ParticipantDto(
                p.placement(),
                p.level(),
                p.goldLeft(),
                p.lastRound(),
                p.playersEliminated(),
                p.totalDamageToPlayers(),
                puuid.equals(p.puuid()),
                nullSafe(p.augments()),
                traits,
                units);
    }

    private StatsDto computeStats(List<MatchSummaryDto> matches) {
        // Double Up placements go from 1 to 4, so they would distort the stats
        List<Integer> placements = matches.stream()
                .filter(m -> !DOUBLE_UP.equals(m.queue()))
                .map(m -> m.player().placement())
                .toList();

        if (placements.isEmpty()) {
            return new StatsDto(0, 0, 0, 0);
        }

        int games = placements.size();
        double average = placements.stream().mapToInt(Integer::intValue).average().orElse(0);
        long top4 = placements.stream().filter(p -> p <= 4).count();
        long wins = placements.stream().filter(p -> p == 1).count();

        return new StatsDto(games, round1(average), round1(top4 * 100.0 / games), round1(wins * 100.0 / games));
    }

    private static String queueName(int queueId) {
        return switch (queueId) {
            case 1090 -> "Normal";
            case 1100 -> "Ranked";
            case 1130 -> "Hyper Roll";
            case 1160 -> DOUBLE_UP;
            default -> "Other";
        };
    }

    private static double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private static <T> List<T> nullSafe(List<T> list) {
        return list == null ? List.of() : list;
    }
}