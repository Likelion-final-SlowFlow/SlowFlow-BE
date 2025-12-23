package com.slowflow.slowflowbackend.member.repository;

import com.slowflow.slowflowbackend.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<Member> findByUsername(String username);

    @Modifying
    @Query("update Member m set m.goalScore = :goalScore where m.id = :memberId")
    void updateGoalScore(@Param("memberId") Long memberId,
                         @Param("goalScore") int goalScore);
}