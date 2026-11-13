package com.yeriix.tftstats.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "match_participants")
public class MatchParticipantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "match_id", nullable = false, length = 32)
    private String matchId;

    @Column(nullable = false, length = 100)
    private String puuid;

    @Column(nullable = false)
    private int placement;

    protected MatchParticipantEntity() {
    }

    public MatchParticipantEntity(String matchId, String puuid, int placement) {
        this.matchId = matchId;
        this.puuid = puuid;
        this.placement = placement;
    }

    public Long getId() {
        return id;
    }

    public String getMatchId() {
        return matchId;
    }

    public String getPuuid() {
        return puuid;
    }

    public int getPlacement() {
        return placement;
    }
}