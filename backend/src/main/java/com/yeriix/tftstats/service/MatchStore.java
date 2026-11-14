package com.yeriix.tftstats.service;

import com.yeriix.tftstats.dto.riot.RiotMatch;
import com.yeriix.tftstats.entity.MatchEntity;
import com.yeriix.tftstats.entity.MatchParticipantEntity;
import com.yeriix.tftstats.repository.MatchParticipantRepository;
import com.yeriix.tftstats.repository.MatchRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Optional;

@Service
public class MatchStore {

    private final MatchRepository matchRepository;
    private final MatchParticipantRepository participantRepository;
    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    public MatchStore(MatchRepository matchRepository, MatchParticipantRepository participantRepository) {
        this.matchRepository = matchRepository;
        this.participantRepository = participantRepository;
    }

    public boolean exists(String matchId) {
        return matchRepository.existsById(matchId);
    }

    @Transactional
    public RiotMatch save(String rawJson) {
        RiotMatch match = parse(rawJson);
        String matchId = match.metadata().matchId();

        if (!matchRepository.existsById(matchId)) {
            RiotMatch.Info info = match.info();
            matchRepository.save(new MatchEntity(
                    matchId,
                    info.gameDatetime(),
                    info.queueId(),
                    info.tftSetNumber(),
                    (int) Math.round(info.gameLength()),
                    rawJson));

            List<MatchParticipantEntity> participants = info.participants().stream()
                    .map(p -> new MatchParticipantEntity(matchId, p.puuid(), p.placement()))
                    .toList();
            participantRepository.saveAll(participants);
        }
        return match;
    }

    @Transactional(readOnly = true)
    public List<RiotMatch> findRecent(String puuid, int limit) {
        return matchRepository.findByPlayer(puuid, PageRequest.of(0, limit)).stream()
                .map(entity -> parse(entity.getRawJson()))
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<RiotMatch> find(String matchId) {
        return matchRepository.findById(matchId).map(entity -> parse(entity.getRawJson()));
    }

    private RiotMatch parse(String rawJson) {
        return jsonMapper.readValue(rawJson, RiotMatch.class);
    }
}