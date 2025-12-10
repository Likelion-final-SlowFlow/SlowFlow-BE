package com.slowflow.SlowFlow_backend.feedback.model;

import com.slowflow.SlowFlow_backend.score.model.DailyState;
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
    private DailyState state;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
}
