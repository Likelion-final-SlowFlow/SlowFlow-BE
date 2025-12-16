package com.slowflow.slowflowbackend.score.repository;

import com.slowflow.slowflowbackend.score.model.DailyScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailyScoreRepository extends JpaRepository<DailyScore, Long> {

    List<DailyScore> findByMemberIdAndDateBetween(Long memberId, LocalDate start, LocalDate end);

    boolean existsByMemberIdAndDateBefore(Long memberId, LocalDate date);
}