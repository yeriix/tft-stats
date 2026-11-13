package com.yeriix.tftstats.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "matches")
public class MatchEntity {

    @Id
    @Column(name = "match_id", length = 32)
    private String matchId;

    @Column(name = "game_datetime", nullable = false)
    private long gameDatetime;

    @Column(name = "queue_id", nullable = false)
    private int queueId;

    @Column(name = "set_number", nullable = false)
    private int setNumber;

    @Column(name = "game_length_seconds", nullable = false)
    private int gameLengthSeconds;

    @Column(name = "raw_json", nullable = false, columnDefinition = "LONGTEXT")
    private String rawJson;

    protected MatchEntity() {
    }

    public MatchEntity(String matchId, long gameDatetime, int queueId, int setNumber,
                       int gameLengthSeconds, String rawJson) {
        this.matchId = matchId;
        this.gameDatetime = gameDatetime;
        this.queueId = queueId;
        this.setNumber = setNumber;
        this.gameLengthSeconds = gameLengthSeconds;
        this.rawJson = rawJson;
    }

    public String getMatchId() {
        return matchId;
    }

    public long getGameDatetime() {
        return gameDatetime;
    }

    public int getQueueId() {
        return queueId;
    }

    public int getSetNumber() {
        return setNumber;
    }

    public int getGameLengthSeconds() {
        return gameLengthSeconds;
    }

    public String getRawJson() {
        return rawJson;
    }
}