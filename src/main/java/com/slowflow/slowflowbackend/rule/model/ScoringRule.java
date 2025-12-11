package com.slowflow.slowflowbackend.rule.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ScoringRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleCategory category; // DIET / EXERCISE / SLEEP

    @Column(nullable = false, unique = true)
    private String ruleKey; // HIGH_FAT_MANY, WALK_SHORT 등 식별자

    @Column(nullable = false)
    private String description; // 설명

    @Column(nullable = false)
    private int score; // -120 ~ +60
}