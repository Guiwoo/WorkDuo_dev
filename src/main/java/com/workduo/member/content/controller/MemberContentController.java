package com.workduo.member.content.controller;

import com.workduo.common.CommonResponse;
import com.workduo.error.global.exception.CustomMethodArgumentNotValidException;
import com.workduo.member.content.dto.*;
import com.workduo.member.content.service.MemberContentService;
import com.workduo.util.ApiUtils;
import com.workduo.util.ApiUtils.ApiResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

;import static com.workduo.util.ApiUtils.success;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/member/content")
public class MemberContentController {

    private final MemberContentService memberContentService;

    // 피드 생성
    @PostMapping("")
    public ApiResult<?> apiCreateContent(
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
        return success(null);
    }

    // 피드 리스트
    @GetMapping("/list")
    public ApiResult<?> getContents(Pageable pageable){
        Page<MemberContentListDto> contentList = memberContentService.getContentList(pageable);
        return success(contentList);
    }

    // 피드 상세
    @GetMapping("{memberContentId}")
    public ApiResult<?> getSpecificContent(
            @PathVariable("memberContentId") Long memberContentId){
        MemberContentDetailDto contentDetail = memberContentService.getContentDetail(memberContentId);
        return success(contentDetail);
    }

    //피드 수정
    @PatchMapping("{memberContentId}")
    public ApiResult<?> updateContent(
            @PathVariable("memberContentId") Long memberContentId,
            @RequestBody @Valid ContentUpdate.Request req,
            BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new CustomMethodArgumentNotValidException(bindingResult);
        }
        memberContentService.contentUpdate(memberContentId,req);
        return success(null);
    }

    // 피드삭제
    @DeleteMapping("{memberContentId}")
    public ApiResult<?> deleteContent(
            @PathVariable("memberContentId") Long contentId
    ){
        memberContentService.contentDelete(contentId);
        return success(null);
    }

    // 피드 좋아요
    @PostMapping("{memberContentId}/like")
    public ApiResult<?> contentLike(
            @PathVariable("memberContentId") Long contentId
    ){
        memberContentService.contentLike(contentId);
        return success(null);
    }

    // 피드 좋아요 취소
    @DeleteMapping("{memberContentId}/like")
    public ApiResult<?> contentLikeCancel(
            @PathVariable("memberContentId") Long contentId
    ){
        memberContentService.contentLikeCancel(contentId);
        return success(null);
    }

    // 피드 컨탠트  코멘트 작성
    @PostMapping("{memberContentId}/comment")
    public ApiResult<?> contentComment(
            @PathVariable("memberContentId") Long contentId,
            @RequestBody @Valid ContentCommentCreate.Request req,
            BindingResult bindingResult){

        if(bindingResult.hasErrors()){
        throw new CustomMethodArgumentNotValidException(bindingResult);
        }

        memberContentService.contentCommentCreate(req,contentId);
        return success(null);
    }

     // 피드 커맨트 리스트
    @GetMapping("{memberContentId}/comment")
    public ApiResult<?> getCommentList(
            Pageable pageable,
            @PathVariable("memberContentId") Long memberContentId){

        return success(memberContentService
                .getContentCommentList(memberContentId, pageable));
    }

    // 피드 댓글 업데이트
    @PatchMapping("{memberContentId}/comment/{commentId}")
    public ApiResult<?> getContentComment(
            @PathVariable("memberContentId") Long memberContentId,
            @PathVariable("commentId") Long commentId,
            @RequestBody ContentCommentUpdate.Request req,
            BindingResult bindingResult

    ){
        if(bindingResult.hasErrors()){
            throw new CustomMethodArgumentNotValidException(bindingResult);
        }

        memberContentService.contentCommentUpdate(memberContentId,commentId,req);

        return success(null);
    }

    //피드 댓글 삭제
    @DeleteMapping("{memberContentId}/comment/{commentId}")
    public ApiResult<?> deleteContentComment(
            @PathVariable("memberContentId") Long memberContentId,
            @PathVariable("commentId") Long commentId
    ){
        memberContentService.contentCommentDelete(memberContentId,commentId);
        return success(null);
    }
    //피드 댓글 좋아요
    @PostMapping("{memberContentId}/comment/{commentId}/like")
    public ApiResult<?> contentCommentLike(
            @PathVariable("memberContentId") Long contentId,
            @PathVariable("commentId") Long commentId
    ){
        memberContentService.contentCommentLike(contentId,commentId);
        return success(null);
    }
    //피드 댓글 좋아요 취소
    @DeleteMapping("{memberContentId}/comment/{commentId}/like")
    public ApiResult<?> contentCommentLikeCancel(
            @PathVariable("memberContentId") Long contentId,
            @PathVariable("commentId") Long commentId
    ){
        memberContentService.contentCommentLikeCancel(contentId,commentId);
        return success(null);
    }
}
