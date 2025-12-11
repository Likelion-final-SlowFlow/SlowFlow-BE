package com.slowflow.slowflowbackend.member.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    private Long memberId;

    @Column(nullable = false)
    private String token;

    public void updateToken(String token) {
        this.token = token;
    }
}