package com.yeriix.tftstats.repository;

import com.yeriix.tftstats.entity.MatchEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchRepository extends JpaRepository<MatchEntity, String> {

    @Query("""
            select m from MatchEntity m
            join MatchParticipantEntity p on p.matchId = m.matchId
            where p.puuid = :puuid
            order by m.gameDatetime desc
            """)
    List<MatchEntity> findByPlayer(@Param("puuid") String puuid, Pageable pageable);
}