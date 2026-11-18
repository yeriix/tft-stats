package com.yeriix.tftstats.service;

import com.yeriix.tftstats.client.RiotClient;
import com.yeriix.tftstats.dto.AccountDto;
import com.yeriix.tftstats.dto.ItemDto;
import com.yeriix.tftstats.dto.MatchDetailDto;
import com.yeriix.tftstats.dto.MatchPageDto;
import com.yeriix.tftstats.dto.MatchSummaryDto;
import com.yeriix.tftstats.dto.ParticipantDto;
import com.yeriix.tftstats.dto.PlayerProfileDto;
import com.yeriix.tftstats.dto.RankDto;
import com.yeriix.tftstats.dto.SpriteDto;
import com.yeriix.tftstats.dto.StatsDto;
import com.yeriix.tftstats.dto.TraitDto;
import com.yeriix.tftstats.dto.UnitDto;
import com.yeriix.tftstats.dto.riot.RiotMatch;
import com.yeriix.tftstats.dto.riot.SummonerDto;
import com.yeriix.tftstats.staticdata.ProfileIconService;
import com.yeriix.tftstats.staticdata.StaticDataFile;
import com.yeriix.tftstats.staticdata.StaticDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TftService {

    private static final Logger log = LoggerFactory.getLogger(TftService.class);

    private static final int PAGE_SIZE = 20;
    private static final int MAX_PAGE = 4; // up to 100 games
    private static final int MAX_MATCHES_SHOWN = (MAX_PAGE + 1) * PAGE_SIZE;
    private static final String RANKED_QUEUE = "RANKED_TFT";
    private static final String DOUBLE_UP = "Double Up";

    private final RiotClient riotClient;
    private final MatchStore matchStore;
    private final StaticDataService staticData;
    private final ProfileIconService profileIcons;
    private final Set<String> unknownIds = ConcurrentHashMap.newKeySet();

    public TftService(RiotClient riotClient, MatchStore matchStore,
                      StaticDataService staticData, ProfileIconService profileIcons) {
        this.riotClient = riotClient;
        this.matchStore = matchStore;
        this.staticData = staticData;
        this.profileIcons = profileIcons;
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

        Optional<SummonerDto> summoner = findSummoner(puuid);

        return new PlayerProfileDto(
                puuid,
                account.gameName(),
                account.tagLine(),
                rank,
                summoner.map(s -> profileIcons.iconUrl(s.profileIconId())).orElse(null),
                summoner.map(SummonerDto::summonerLevel).orElse(0L));
    }

    /**
     * Makes sure the requested page of history is stored in the database and returns
     * every stored match of the player for the selected mode, together with its stats.
     */
    public MatchPageDto getMatchPage(String puuid, String mode, int page) {
        int safePage = Math.min(Math.max(page, 0), MAX_PAGE);
        boolean riotHasMore = syncPage(puuid, safePage);

        List<MatchSummaryDto> matches = matchStore.findRecent(puuid, queueIdFor(mode), MAX_MATCHES_SHOWN).stream()
                .map(match -> toSummary(match, puuid))
                .flatMap(Optional::stream)
                .toList();

        return new MatchPageDto(computeStats(matches), matches, riotHasMore && safePage < MAX_PAGE);
    }

    public MatchDetailDto getMatchDetail(String matchId, String puuid) {
        RiotMatch match = matchStore.find(matchId)
                .orElseGet(() -> matchStore.save(riotClient.getTftMatchRaw(matchId)));
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

    /** The icon is a nice extra, so a failure here must not break the whole profile. */
    private Optional<SummonerDto> findSummoner(String puuid) {
        try {
            return Optional.ofNullable(riotClient.getSummoner(puuid));
        } catch (RestClientResponseException e) {
            log.warn("Could not load summoner data (HTTP {}), showing the profile without icon",
                    e.getStatusCode().value());
            return Optional.empty();
        }
    }

    /** Downloads from Riot the matches of one history page that are not stored yet. */
    private boolean syncPage(String puuid, int page) {
        List<String> ids = riotClient.getTftMatchIds(puuid, page * PAGE_SIZE, PAGE_SIZE);
        int fetched = 0;
        for (String matchId : ids) {
            if (!matchStore.exists(matchId)) {
                matchStore.save(riotClient.getTftMatchRaw(matchId));
                fetched++;
            }
        }
        log.info("Page {} synced: {} new matches fetched from Riot, the rest came from the database", page, fetched);
        return ids.size() == PAGE_SIZE;
    }

    private static Integer queueIdFor(String mode) {
        return switch (mode == null ? "all" : mode) {
            case "ranked" -> 1100;
            case "normal" -> 1090;
            case "hyper-roll" -> 1130;
            case "double-up" -> 1160;
            default -> null;
        };
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
                .map(this::toTrait)
                .toList();

        List<UnitDto> units = nullSafe(p.units()).stream()
                .map(this::toUnit)
                .sorted(Comparator.comparingInt(UnitDto::cost).thenComparingInt(UnitDto::tier).reversed())
                .toList();

        return new ParticipantDto(
                p.placement(),
                p.level(),
                p.goldLeft(),
                p.lastRound(),
                puuid.equals(p.puuid()),
                traits,
                units);
    }

    private UnitDto toUnit(RiotMatch.Unit u) {
        Optional<StaticDataFile.Entry> champion = staticData.champion(u.characterId());
        warnIfUnknown(champion, "champion", u.characterId());

        return new UnitDto(
                u.characterId(),
                champion.map(StaticDataFile.Entry::name)
                        .orElseGet(() -> StaticDataService.fallbackName(u.characterId())),
                champion.map(StaticDataFile.Entry::cost).orElse(0),
                u.tier(),
                champion.map(StaticDataFile.Entry::image).map(TftService::toSprite).orElse(null),
                nullSafe(u.itemNames()).stream().map(this::toItem).toList());
    }

    private ItemDto toItem(String itemId) {
        Optional<StaticDataFile.Entry> item = staticData.item(itemId);
        warnIfUnknown(item, "item", itemId);

        return new ItemDto(
                itemId,
                item.map(StaticDataFile.Entry::name).orElseGet(() -> StaticDataService.fallbackName(itemId)),
                item.map(StaticDataFile.Entry::image).map(TftService::toSprite).orElse(null));
    }

    private TraitDto toTrait(RiotMatch.Trait t) {
        Optional<StaticDataFile.Entry> trait = staticData.trait(t.name());
        warnIfUnknown(trait, "trait", t.name());

        return new TraitDto(
                trait.map(StaticDataFile.Entry::name).orElseGet(() -> StaticDataService.fallbackName(t.name())),
                t.numUnits(),
                t.style(),
                trait.map(StaticDataFile.Entry::image).map(StaticDataFile.Image::full).orElse(null));
    }

    private static SpriteDto toSprite(StaticDataFile.Image image) {
        return new SpriteDto(image.sprite(), image.x(), image.y(), image.w(), image.h());
    }

    private void warnIfUnknown(Optional<?> found, String kind, String id) {
        if (found.isEmpty() && unknownIds.add(kind + ":" + id)) {
            log.warn("No static data found for {} '{}'", kind, id);
        }
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