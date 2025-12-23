package com.slowflow.slowflowbackend.member.service;

import com.slowflow.slowflowbackend.member.model.Member;
import com.slowflow.slowflowbackend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GoalService {

    private final MemberRepository memberRepository;

    public void updateGoal(Member member, int goalScore) {
        memberRepository.updateGoalScore(member.getId(), goalScore);
    }
}