package com.workduo.member.member.service;

import com.workduo.member.member.dto.MemberDto;
import com.workduo.member.member.dto.createmember.CreateMember;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface MemberService extends UserDetailsService {

    MemberDto register(CreateMember.Request request);
    MemberDto findMember(Long id);
}
