package com.workduo.member.member.service.impl;

import com.workduo.area.siggarea.entity.SiggArea;
import com.workduo.error.member.exception.MemberException;
import com.workduo.error.member.type.MemberErrorCode;
import com.workduo.member.member.dto.MemberLoginDto;
import com.workduo.member.member.entity.Member;
import com.workduo.member.member.repository.MemberRepository;
import com.workduo.member.member.type.MemberStatus;
import com.workduo.member.memberrole.repository.MemberRoleRepository;
import com.workduo.sport.sport.entity.Sport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    MemberRepository memberRepository;
    @Mock
    private MemberRoleRepository memberRoleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    MemberServiceImpl memberService;


    @BeforeEach
    public void init(){}

    @Test
    @DisplayName("유저 검증 [메일이 없는경우]")
    void failAuthenticateDoesNotExistEmail(){
        MemberLoginDto.Request req = MemberLoginDto.Request.builder()
                .email("abc")
                .password("something")
                .build();

        //when
        MemberException exception =  assertThrows(MemberException.class,
                ()->memberService.authenticateUser(req));
        //then
        assertEquals(MemberErrorCode.MEMBER_EMAIL_ERROR,exception.getErrorCode());
    }

    @Test
    @DisplayName("유저 검증 [정지된 회원]")
    void failAuthenticateBannedUser(){
        MemberLoginDto.Request req = MemberLoginDto.Request.builder()
                .email("abc")
                .password("something")
                .build();
        //given
        given(memberRepository.findByEmail(any())).willReturn(
                Optional.of(Member.builder().memberStatus(MemberStatus.MEMBER_STATUS_STOP).build())
        );
        //when
        MemberException exception =  assertThrows(MemberException.class,
                ()->memberService.authenticateUser(req));
        //then
        assertEquals(MemberErrorCode.MEMBER_STOP_ERROR,exception.getErrorCode());
    }

    @Test
    @DisplayName("유저 검증 [탈퇴한 회원]")
    void failAuthenticateWithdrawUser(){
        MemberLoginDto.Request req = MemberLoginDto.Request.builder()
                .email("abc")
                .password("something")
                .build();
        //given
        given(memberRepository.findByEmail(any())).willReturn(
                Optional.of(Member.builder().memberStatus(MemberStatus.MEMBER_STATUS_WITHDRAW).build())
        );
        //when
        MemberException exception =  assertThrows(MemberException.class,
                ()->memberService.authenticateUser(req));
        //then
        assertEquals(MemberErrorCode.MEMBER_WITHDRAW_ERROR,exception.getErrorCode());
    }

    @Test
    @DisplayName("유저 검증 [비밀번호가 다름]")
    void failAuthenticatePasswordDifferent(){
        MemberLoginDto.Request req = MemberLoginDto.Request.builder()
                .email("test@test.com")
                .password("something")
                .build();
        //given
        given(memberRepository.findByEmail(any())).willReturn(
                Optional.of(Member.builder().memberStatus(MemberStatus.MEMBER_STATUS_ING).build())
        );
        given(passwordEncoder.matches(any(),any())).willReturn(false);
        //when
        MemberException exception =  assertThrows(MemberException.class,
                ()->memberService.authenticateUser(req));
        //then
        assertEquals(MemberErrorCode.MEMBER_PASSWORD_ERROR,exception.getErrorCode());
    }

    @Test
    @DisplayName("유저 롤 받아오기")
    void successGetMemberRole(){
        MemberLoginDto.Request req = MemberLoginDto.Request.builder()
                .email("test@test.com")
                .password("something")
                .build();
        Member m = Member.builder().memberStatus(MemberStatus.MEMBER_STATUS_ING).build();
        //given
        given(memberRepository.findByEmail(any())).willReturn(
                Optional.of(m)
        );
        given(passwordEncoder.matches(any(),any())).willReturn(true);
        memberService.authenticateUser(req);
        verify(memberRoleRepository,times(1)).findByMember(m);
    }
}