package com.workduo.member.member.service.impl;

import com.workduo.member.member.dto.MemberDto;
import com.workduo.member.member.dto.createmember.CreateMember;
import com.workduo.member.member.entity.Member;
import com.workduo.member.member.repository.MemberRepository;
import com.workduo.member.member.service.MemberService;
import com.workduo.member.member.type.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public MemberDto register(CreateMember.Request request) {

        Member member = Member.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .phoneNumber(request.getPhoneNumber())
                .password(request.getPassword())
                .nickname(request.getNickname())
                .memberStatus(MemberStatus.MEMBER_STATUS_ING)
                .build();

        return MemberDto.fromEntity(memberRepository.save(member));
    }

    @Override
    public MemberDto findMember(Long id) {
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
