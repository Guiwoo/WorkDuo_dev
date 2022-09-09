package com.workduo.configuration.jwt.memberrefreshtoken.service;

import com.workduo.configuration.jwt.JwtAuthenticationFilter;
import com.workduo.configuration.jwt.TokenProvider;
import com.workduo.configuration.jwt.memberrefreshtoken.entity.MemberRefreshToken;
import com.workduo.configuration.jwt.memberrefreshtoken.repository.MemberRefreshTokenRepository;
import com.workduo.error.member.exception.MemberException;
import com.workduo.error.member.type.MemberErrorCode;
import com.workduo.member.member.dto.auth.MemberAuthenticateDto;
import com.workduo.member.member.entity.Member;
import com.workduo.member.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public String validateRefreshToken(MemberAuthenticateDto m){
        try{
            MemberRefreshToken memberRefreshToken = getRefreshToken(m.getEmail());
            //기간넘는경우
            if(memberRefreshToken.getExpireDate().isBefore(LocalDateTime.now())){
                memberRefreshToken.updateExpireDate();
            }
        }catch (MemberException e){
            log.error("Error code : {}  , Error MEssage : {}", e.getErrorCode(),e.getErrorMessage());
            //없어 ? 생성해
            this.generateRefreshToken(m.getEmail());
        }
        return tokenProvider.generateToken(m.getEmail(), m.getRoles());
    }

    /**
     * refresh token이 있는 경우 만료날짜에 따라 만료날짜 업데이트
     * @param member
     */
    @Transactional
    public void searchMemberRefreshToken(Member member) {
        MemberRefreshToken refreshToken = getRefreshToken(member.getEmail());
        if (refreshToken.getExpireDate().isBefore(LocalDateTime.now())) {
            refreshToken.updateExpireDate();
        }
    }

    /**
     * refresh token이 없는 경우 생성
     * controller로 exception을 던져서 거기서 refresh token 생성하는 generateRefreshToken 를 호출 후 반환
     * @param email
     */
    @Transactional
    public void generateRefreshToken(String email){
        String token = UUID.randomUUID().toString();
        MemberRefreshToken refreshToken = MemberRefreshToken.builder()
                .memberEmail(email)
                .refreshToken(token)
                .expireDate(LocalDateTime.now().plusYears(1))
                .build();
        memberRefreshTokenRepository.save(refreshToken);
    }
//
//    private Member getMember(String email) {
//        return memberRepository.findByEmail(email)
//                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_EMAIL_ERROR));
//    }
    private MemberRefreshToken getRefreshToken(String email) {
        return memberRefreshTokenRepository.findByMemberEmail(email).orElseThrow(
                ()->new MemberException(MemberErrorCode.MEMBER_REFRESH_TOKEN_ERROR));
    }
}
