package com.slowflow.slowflowbackend.action.repository;

import com.slowflow.slowflowbackend.action.model.FillAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FillActionRepository extends JpaRepository<FillAction, Long> {
    @Query(value = "SELECT * FROM fill_action WHERE score > 0 ORDER BY random() LIMIT 3", nativeQuery = true)
    List<FillAction> findRandomTop3Positive();
}
