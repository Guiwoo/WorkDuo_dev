package com.workduo.member.member.controller;

import com.workduo.configuration.jwt.TokenProvider;
import com.workduo.configuration.jwt.memberrefreshtoken.service.MemberRefreshService;
import com.workduo.group.group.service.GroupService;
import com.workduo.member.history.service.LoginHistoryService;
import com.workduo.member.member.dto.*;
import com.workduo.member.member.dto.auth.MemberAuthenticateDto;
import com.workduo.member.member.service.MemberService;
import com.workduo.util.ApiUtils.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static com.workduo.util.ApiUtils.success;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@Tag(name="멤버 서비스",description = "멤버 등록,수정,탈퇴,로그인 관련 API 입니다.")
public class MemberController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;
    private final MemberRefreshService refreshService;
    private final LoginHistoryService loginHistoryService;
    private final GroupService groupService;

    //로그인
    @PostMapping("/login")
    @Operation(summary = "로그인 이 가능합니다." ,description = "token 을 발급받아, 서비스 이용 이 가능합니다.")
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
    @Operation(summary = "회원가입 이 가능합니다." ,description = "회원가입 이 가능합니다.")
    public ApiResult<?> apiCreate(
            @RequestBody @Validated MemberCreate.Request req
    ){
        memberService.createUser(req);
        return success(null);
    }
    //멤버 상세 정보
    @GetMapping("{memberId}")
    @Operation(summary = "프로필 정보 를 받을수 있습니다." ,description = "가입된 회원 누구라도 프로필을 확인할수 있습니다.")
    public ApiResult<?> apiGEt(
            @PathVariable("memberId") @Parameter(example = "1",description = "멤버의 아이디가 필요합니다.") Long memberId,
            Pageable pageable
    ){
        MemberProfileDto user = memberService.getUser(memberId, pageable);
        return success(user);
    }

    //회원정보수정
    @PatchMapping("")
    @Operation(summary = "회원정보 수정 이 가능 합니다." ,description = "가입된 회원 누구라도 본인정보를 수정할수 있습니다.")
    public ApiResult<?> apiEdit(
            @Validated MemberEdit.Request req
    ){
        memberService.editUser(req);
        return success(null);
    }
    //이미지 업데이트 api
    @PatchMapping("image")
    @Operation(summary = "이미지 업데이트 가 가능합니다." ,description = "본인의 프로필 이미지를 업데이트 할수 있습니다.")
    public ApiResult<?> apiProfileImageEdit(
            MultipartFile multipartFileList
    ){
        memberService.updateImage(multipartFileList);
        return success(null);
    }

    //비밀번호 변경
    @PatchMapping("/password")
    @Operation(summary = "비밀번호 변경 이 가능합니다." ,description = "비밀번호 를 업데이트 할수 있습니다.")
    public ApiResult<?> apiPasswordEdit(
            @RequestBody @Validated MemberChangePassword.Request req
    ){

        memberService.changePassword(req);

        return success(null);
    }

    //회원탈퇴
    @DeleteMapping("")
    @Operation(summary = "회원탈퇴 가 가능합니다." ,description = "회원탈퇴 할수 있습니다.")
    public ApiResult<?> apiDelete(){
        memberService.withdraw(groupService);
        return success(null);
    }
}
