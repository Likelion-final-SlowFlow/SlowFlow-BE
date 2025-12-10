package com.slowflow.SlowFlow_backend.action.model;

import com.slowflow.SlowFlow_backend.member.model.Member;
import com.slowflow.SlowFlow_backend.rule.model.RuleCategory;
import com.slowflow.SlowFlow_backend.rule.model.ScoringRule;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleCategory category;

    @Column(nullable = false)
    private String rawText; // 사용자가 입력한 텍스트

    @Column(columnDefinition = "TEXT")
    private String parsedJson; // LLM 파싱 결과

    private int score; // 최종 점수

    private String reason; // 왜 이 점수가 적용되었는지 설명

    private LocalDate date; // 일일 점수 계산용 날짜

    private LocalDateTime createdAt; // 행동이 생성된 실제 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id")
    private ScoringRule rule;
}
