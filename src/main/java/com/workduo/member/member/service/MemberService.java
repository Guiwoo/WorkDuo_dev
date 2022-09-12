package com.workduo.member.member.service;

import com.workduo.member.member.dto.MemberCreate;
import com.workduo.member.member.dto.MemberEdit;
import com.workduo.member.member.dto.MemberLogin;
import com.workduo.member.member.dto.auth.MemberAuthenticateDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface MemberService extends UserDetailsService {
    MemberAuthenticateDto authenticateUser(MemberLogin.Request member);
    void createUser(MemberCreate.Request create);
    void editUser(MemberEdit.Request req);
}
