package com.slowflow.SlowFlow_backend.member.model;

import com.slowflow.SlowFlow_backend.action.model.UserAction;
import com.slowflow.SlowFlow_backend.score.model.DailyScore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    private int goalScore;

    @OneToMany(mappedBy = "member")
    private List<UserAction> actions = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<DailyScore> dailyScores = new ArrayList<>();
}
