package com.workduo.member.content.controller;

import com.workduo.error.global.exception.CustomMethodArgumentNotValidException;
import com.workduo.member.content.dto.ContentCreate;
import com.workduo.member.content.service.MemberContentService;
import com.workduo.member.member.dto.MemberLogin;
import com.workduo.member.member.dto.auth.MemberAuthenticateDto;
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
import org.springframework.web.multipart.MultipartFile;
;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/member/content")
public class MemberContentController {

    private final MemberContentService memberContentService;

    @PostMapping("")
    public ResponseEntity<?> apiCreateContent(
            List<MultipartFile> multipartFiles,
            @Validated ContentCreate.Request req,
            BindingResult bindingResult
    ) throws Exception {
        if(bindingResult.hasErrors()){
            throw new CustomMethodArgumentNotValidException(bindingResult);
        }
        // 추후 업데이트 가 필요
        if (multipartFiles != null && multipartFiles.size() > 5) {
            throw new RuntimeException("사진은 최대 5장까지 업로드 가능합니다.");
        }

        memberContentService.createContent(req,multipartFiles);

        return new ResponseEntity<>(MemberLogin.Response.builder()
                .success("T")
                .build(), HttpStatus.OK);
    }
}
