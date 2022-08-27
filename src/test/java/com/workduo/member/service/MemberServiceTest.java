package com.workduo.member.service;


import com.workduo.member.member.dto.MemberDto;
import com.workduo.member.member.dto.createmember.CreateMember;
import com.workduo.member.member.entity.Member;
import com.workduo.member.member.repository.MemberRepository;
import com.workduo.member.member.service.MemberService;
import com.workduo.member.member.service.impl.MemberServiceImpl;
import com.workduo.member.member.type.MemberStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    @Test
    @DisplayName("member register")
    public void member_register() throws Exception {
        // given
        CreateMember.Request request = CreateMember.Request.builder()
                .email("rbsks147@naver.com")
                .username("한규빈")
                .phoneNumber("010-4046-3138")
                .password("1234")
                .nickname("규난")
                .build();

        Member member = Member.builder()
                .email("rbsks147@naver.com")
                .username("한규빈")
                .phoneNumber("010-4046-3138")
                .password("1234")
                .nickname("규난")
                .memberStatus(MemberStatus.MEMBER_STATUS_ING)
                .build();

        given(memberRepository.save(any()))
                .willReturn(member);

        // when
        MemberDto register = memberService.register(request);

        // then
        assertEquals(member.getEmail(), register.getEmail());
    }
}