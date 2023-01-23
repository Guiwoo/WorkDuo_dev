package com.workduo.member.member.service;

import com.core.domain.member.dto.MemberEdit;
import com.group.group.service.GroupService;
import com.workduo.member.member.dto.MemberChangePassword;
import com.workduo.member.member.dto.MemberCreate;
import com.workduo.member.member.dto.MemberLogin;
import com.workduo.member.member.dto.MemberProfileDto;
import com.workduo.member.member.dto.auth.MemberAuthenticateDto;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService extends UserDetailsService {
    MemberAuthenticateDto authenticateUser(MemberLogin.Request member);
    void createUser(MemberCreate.Request create);
    void editUser(MemberEdit.Request req);
    void changePassword(MemberChangePassword.Request req);
    void withdraw(GroupService groupService);
    MemberProfileDto getUser(Long memberId, Pageable pageable);

    void updateImage(MultipartFile multipartFileList);
}
