package com.yeriix.tftstats.repository;

import com.yeriix.tftstats.entity.MatchParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchParticipantRepository extends JpaRepository<MatchParticipantEntity, Long> {
}