package com.workduo.configuration.jwt.memberrefreshtoken.service;

import com.workduo.configuration.jwt.JwtAuthenticationFilter;
import com.workduo.configuration.jwt.TokenProvider;
import com.workduo.configuration.jwt.memberrefreshtoken.entity.MemberRefreshToken;
import com.workduo.configuration.jwt.memberrefreshtoken.repository.MemberRefreshTokenRepository;
import com.workduo.member.member.dto.authDto.MemberRoleAuthDto;
import com.workduo.member.member.entity.Member;
import com.workduo.member.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberRefreshService {
    private final MemberRefreshTokenRepository memberRefreshTokenRepository;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Transactional
    public String validateToken(HttpServletRequest request){
        String token = jwtAuthenticationFilter.resolveTokenFromRequest(request);
        if (!StringUtils.hasText(token)) {
            throw new IllegalStateException("not authorization");
        }

        Authentication authentication = tokenProvider.getAuthentication(token);
        UserDetails userDetails = (UserDetails) authentication;

        Member member = getMember(userDetails.getUsername());
        MemberRefreshToken memberRefreshToken = getRefreshToken(member);

        if(memberRefreshToken.getExpiredAt().isBefore(LocalDateTime.now())){
            memberRefreshToken.updateExpireDate();
        }


        return tokenProvider.generateToken(userDetails.getUsername(), tokenProvider.getKeyRoles(token));
    }

    /**
     * refresh token이 있는 경우 만료날짜에 따라 만료날짜 업데이트
     * @param member
     */
    @Transactional
    public void searchMemberRefreshToken(Member member) {
        MemberRefreshToken refreshToken = getRefreshToken(member);

        if (refreshToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            refreshToken.updateExpireDate();
        }
    }

    /**
     * refresh token이 없는 경우 생성
     * controller로 exception을 던져서 거기서 refresh token 생성하는 generateRefreshToken를 호출 후 반환
     * @param m
     */
    @Transactional
    public void generateRefreshToken(Member m){

        String token = UUID.randomUUID().toString();
        MemberRefreshToken refreshToken = MemberRefreshToken.builder()
                .refreshToken(token)
                .expiredAt(LocalDateTime.now().plusYears(1))
                .build();

        memberRefreshTokenRepository.save(refreshToken);
    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("user not found"));
    }

    private MemberRefreshToken getRefreshToken(Member member) {
        return memberRefreshTokenRepository.findByMemberId(member.getEmail()).orElseThrow(
                ()->new RuntimeException("Refresh Token 이 없습니다."));
    }
}
