package com.slowflow.slowflowbackend.score.model;

import com.slowflow.slowflowbackend.member.model.Member;
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

    public void updateDietPositive(int v) { this.dietPositive = v; }
    public void updateDietNegative(int v) { this.dietNegative = v; }

    public void updateExercisePositive(int v) { this.exercisePositive = v; }
    public void updateExerciseNegative(int v) { this.exerciseNegative = v; }

    public void updateSleepPositive(int v) { this.sleepPositive = v; }
    public void updateSleepNegative(int v) { this.sleepNegative = v; }

    public void updateTotalScore(int v) { this.totalScore = v; }

    public void updateState(DailyState state) { this.state = state; }

    public void updateAiFeedback(String feedback) { this.aiFeedback = feedback; }
}
