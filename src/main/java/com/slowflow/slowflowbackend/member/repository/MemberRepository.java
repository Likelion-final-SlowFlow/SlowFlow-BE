package com.slowflow.slowflowbackend.member.repository;

import com.slowflow.slowflowbackend.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<Member> findByUsername(String username);
}