package com.slowflow.slowflowbackend.feedback.repository;

import com.slowflow.slowflowbackend.feedback.model.Feedback;
import com.slowflow.slowflowbackend.feedback.model.FeedbackType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    // Postgres 랜덤 1개
    @Query(value = "SELECT * FROM feedback WHERE type = :type ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<Feedback> findRandomByType(@Param("type") String type);

    default Optional<Feedback> findRandomByType(FeedbackType type) {
        return findRandomByType(type.name());
    }
}