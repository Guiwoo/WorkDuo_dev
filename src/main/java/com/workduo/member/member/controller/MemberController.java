package com.workduo.member.member.controller;

import com.workduo.configuration.jwt.TokenProvider;
import com.workduo.configuration.jwt.memberrefreshtoken.service.MemberRefreshService;
import com.workduo.error.global.exception.CustomMethodArgumentNotValidException;
import com.workduo.member.member.dto.auth.MemberAuthenticateDto;
import com.workduo.member.member.dto.MemberLoginDto;
import com.workduo.member.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    //로그인
    @PostMapping("/login")
    public ResponseEntity<?> apiLogin(
            @RequestBody @Validated MemberLoginDto.Request req,
            BindingResult bindingResult
            ){
        if(bindingResult.hasErrors()){
            throw new CustomMethodArgumentNotValidException(bindingResult);
        }
        // Authenticate user here with email and password
        MemberAuthenticateDto m = memberService.authenticateUser(req);
        //token 도 만들고
        String token = tokenProvider.generateToken(m.getEmail(),m.getRoles());
        //refresh token 확인 하고 없으면 만들어주고, 있으면 넘어가고 기한 넘으면 업데이트
        refreshService.validateRefreshToken(m);
        Map<String,String> map = new HashMap<>();
        map.put("token",token);

        return new ResponseEntity<>(MemberLoginDto.Response.builder()
                        .success("T")
                        .result(map)
                        .build(), HttpStatus.OK);
    }
    //회원가입
    //로그아웃
    //회원정보수정
    //비밀번호 변경
    //회원탈퇴
}
