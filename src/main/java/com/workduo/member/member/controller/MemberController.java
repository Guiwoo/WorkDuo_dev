package com.workduo.member.member.controller;

import com.workduo.common.CommonResponse;
import com.workduo.configuration.jwt.TokenProvider;
import com.workduo.configuration.jwt.memberrefreshtoken.service.MemberRefreshService;
import com.workduo.error.global.exception.CustomMethodArgumentNotValidException;
import com.workduo.group.group.service.GroupService;
import com.workduo.member.history.service.LoginHistoryService;
import com.workduo.member.member.dto.*;
import com.workduo.member.member.dto.auth.MemberAuthenticateDto;
import com.workduo.member.member.service.MemberService;
import com.workduo.util.ApiUtils;
import com.workduo.util.ApiUtils.ApiResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static com.workduo.util.ApiUtils.*;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;
    private final MemberRefreshService refreshService;
    private final LoginHistoryService loginHistoryService;
    private final GroupService groupService;

    //로그인
    @PostMapping("/login")
    public ApiResult<?> apiLogin(
            HttpServletRequest request,
            @RequestBody @Validated MemberLogin.Request req
            ) throws Exception {

        MemberAuthenticateDto m = memberService.authenticateUser(req);
        String token = tokenProvider.generateToken(m.getEmail(),m.getRoles());
        refreshService.validateRefreshToken(m);
        loginHistoryService.saveLoginHistory(m.getEmail(),request);

        Map<String,String> map = new HashMap<>();
        map.put("token",token);

        return success(token);
    }

    //회원가입
    @PostMapping("")
    public ApiResult<?> apiCreate(
            @RequestBody @Validated MemberCreate.Request req
    ){
        memberService.createUser(req);
        return success(null);
    }

    //회원정보수정
    @PatchMapping("")
    public ApiResult<?> apiEdit(
            @RequestBody @Validated MemberEdit.Request req
    ){
        memberService.editUser(req);
        return success(null);
    }

    //비밀번호 변경
    @PatchMapping("/password")
    public ApiResult<?> apiPasswordEdit(
            @RequestBody @Validated MemberChangePassword.Request req
    ){

        memberService.changePassword(req);

        return success(null);
    }

    //회원탈퇴
    @DeleteMapping("")
    public ApiResult<?> apiDelete(){
        memberService.withdraw(groupService);
        return success(null);
    }
}
