package com.slowflow.slowflowbackend.action.repository;

import com.slowflow.slowflowbackend.action.model.UserAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {

    List<UserAction> findAllByMemberIdAndDateBetweenOrderByDateAscCreatedAtAscIdAsc(
            Long memberId,
            LocalDate start,
            LocalDate end
    );
}