package com.slowflow.slowflowbackend.member.repository;

import com.slowflow.slowflowbackend.member.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}
