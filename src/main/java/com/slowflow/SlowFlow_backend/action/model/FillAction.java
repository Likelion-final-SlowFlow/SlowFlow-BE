package com.slowflow.SlowFlow_backend.action.model;

import com.slowflow.SlowFlow_backend.rule.model.RuleCategory;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FillAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String behavior;

    @Column(nullable = false)
    private int score;

    @Enumerated(EnumType.STRING)
    private RuleCategory category;
}
