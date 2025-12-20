package com.slowflow.slowflowbackend.feedback.model;

import com.slowflow.slowflowbackend.score.model.DailyState;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackType type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
}
