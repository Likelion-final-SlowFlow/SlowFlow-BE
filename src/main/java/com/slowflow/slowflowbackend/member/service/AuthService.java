package com.slowflow.slowflowbackend.member.service;

import com.slowflow.slowflowbackend.global.config.jwt.JwtTokenProvider;
import com.slowflow.slowflowbackend.global.exception.BaseException;
import com.slowflow.slowflowbackend.global.exception.ErrorCode;
import com.slowflow.slowflowbackend.member.dto.LoginRequest;
import com.slowflow.slowflowbackend.member.dto.LoginResponse;
import com.slowflow.slowflowbackend.member.dto.SignupRequest;
import com.slowflow.slowflowbackend.member.model.Member;
import com.slowflow.slowflowbackend.member.model.RefreshToken;
import com.slowflow.slowflowbackend.member.repository.MemberRepository;
import com.slowflow.slowflowbackend.member.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    // 회원가입
    public void signup(SignupRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new BaseException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        if (memberRepository.existsByUsername(request.getUsername())) {
            throw new BaseException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        Member member = Member.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        memberRepository.save(member);
    }

    // 이메일 중복 체크
    public boolean isEmailDuplication(String email) {
        return !memberRepository.existsByEmail(email);
    }

    // 아이디 중복 체크
    public boolean isUsernameDuplication(String username) {
        return !memberRepository.existsByUsername(username);
    }

    // 로그인 중복 체크
    public LoginResponse login(LoginRequest request) {

        Member member = memberRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new BaseException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = tokenProvider.createAccessToken(member.getId());
        String refreshToken = tokenProvider.createRefreshToken(member.getId());

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .memberId(member.getId())
                        .token(refreshToken)
                        .build()
        );

        return new LoginResponse(accessToken, refreshToken);
    }

    // 토큰 재발급
    public LoginResponse reissue(String refreshToken) {

        if (!tokenProvider.isValid(refreshToken)) {
            throw new BaseException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Long memberId = tokenProvider.getMemberId(refreshToken);

        RefreshToken savedToken = refreshTokenRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (!savedToken.getToken().equals(refreshToken)) {
            throw new BaseException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String newAccess = tokenProvider.createAccessToken(memberId);
        String newRefresh = tokenProvider.createRefreshToken(memberId);

        savedToken.updateToken(newRefresh);
        refreshTokenRepository.save(savedToken);

        return new LoginResponse(newAccess, newRefresh);
    }
}