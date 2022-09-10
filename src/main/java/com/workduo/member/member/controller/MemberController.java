package com.workduo.member.member.controller;

import com.workduo.configuration.jwt.TokenProvider;
import com.workduo.configuration.jwt.memberrefreshtoken.service.MemberRefreshService;
import com.workduo.error.global.exception.CustomMethodArgumentNotValidException;
import com.workduo.member.history.service.LoginHistoryService;
import com.workduo.member.member.dto.MemberCreate;
import com.workduo.member.member.dto.MemberLogin;
import com.workduo.member.member.dto.auth.MemberAuthenticateDto;
import com.workduo.member.member.service.MemberService;
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


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;
    private final MemberRefreshService refreshService;
    private final LoginHistoryService loginHistoryService;
    //로그인
    @PostMapping("/login")
    public ResponseEntity<?> apiLogin(
            HttpServletRequest request,
            @RequestBody @Validated MemberLogin.Request req,
            BindingResult bindingResult
            ) throws Exception {
        if(bindingResult.hasErrors()){
            throw new CustomMethodArgumentNotValidException(bindingResult);
        }
        MemberAuthenticateDto m = memberService.authenticateUser(req);
        String token = tokenProvider.generateToken(m.getEmail(),m.getRoles());
        refreshService.validateRefreshToken(m);
        loginHistoryService.saveLoginHistory(m.getEmail(),request);

        Map<String,String> map = new HashMap<>();
        map.put("token",token);

        return new ResponseEntity<>(MemberLogin.Response.builder()
                        .success("T")
                        .result(map)
                        .build(), HttpStatus.OK);
    }
    //회원가입
    @PostMapping("")
    public ResponseEntity<?> apiCreate(
            @RequestBody @Validated MemberCreate.Request req,
            BindingResult bindingResult
    ){
        if(bindingResult.hasErrors()){
            throw new CustomMethodArgumentNotValidException(bindingResult);
        }
        memberService.createUser(req);
        return new ResponseEntity<>(MemberCreate.Response.from(), HttpStatus.OK);
    }

    //회원정보수정
    //비밀번호 변경
    //회원탈퇴
}
