package com.slowflow.SlowFlow_backend.score.model;

import com.slowflow.SlowFlow_backend.member.model.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"member_id", "date"})
        }
)
public class DailyScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    private int dietPositive;
    private int dietNegative;

    private int exercisePositive;
    private int exerciseNegative;

    private int sleepPositive;
    private int sleepNegative;

    @Column(nullable = false)
    private int totalScore;

    @Column(columnDefinition = "TEXT")
    private String aiFeedback;

    @Enumerated(EnumType.STRING)
    private DailyState state; // NONE, NEGATIVE, NEUTRAL, POSITIVE, GOAL_REACHED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
