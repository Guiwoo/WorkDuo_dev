package com.workduo.member.content.controller;

import com.workduo.error.global.exception.CustomMethodArgumentNotValidException;
import com.workduo.member.content.dto.ContentCreate;
import com.workduo.member.content.dto.ContentUpdate;
import com.workduo.member.content.dto.MemberContentDetailDto;
import com.workduo.member.content.dto.MemberContentListDto;
import com.workduo.member.content.service.MemberContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/member/content")
public class MemberContentController {

    private final MemberContentService memberContentService;

    // 피드 생성
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

        return new ResponseEntity<>(
                ContentCreate.Response.from(),
                HttpStatus.OK
                );
    }

    // 피드 리스트
    @GetMapping("/list")
    public ResponseEntity<?> getContents(Pageable pageable){
        Page<MemberContentListDto> contentList = memberContentService.getContentList(pageable);
        return new ResponseEntity<>(
                MemberContentListDto.Response.from(contentList),
                HttpStatus.OK
        );
    }

    // 피드 상세
    @GetMapping("{memberContentId}")
    public ResponseEntity<?> getSpecificContent(
            @PathVariable("memberContentId") Long memberContentId){
        MemberContentDetailDto contentDetail = memberContentService.getContentDetail(memberContentId);
        return new ResponseEntity<>(
                MemberContentDetailDto.Response.from(contentDetail),
                HttpStatus.OK
        );
    }
    //피드 수정
    @PatchMapping("{memberContentId}")
    public ResponseEntity<?> updateContent(
            @PathVariable("memberContentId") Long memberContentId,
            @RequestBody @Valid ContentUpdate.Request req,
            BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new CustomMethodArgumentNotValidException(bindingResult);
        }
        memberContentService.contentUpdate(memberContentId,req);
        return new ResponseEntity<>(
                ContentUpdate.Response.from(),
                HttpStatus.OK
        );
    }

}
