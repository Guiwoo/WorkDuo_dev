package com.workduo.member.member.service;

import com.workduo.member.member.dto.auth.MemberAuthenticateDto;
import com.workduo.member.member.dto.MemberLoginDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface MemberService extends UserDetailsService {
    MemberAuthenticateDto authenticateUser(MemberLoginDto.Request member);
}
