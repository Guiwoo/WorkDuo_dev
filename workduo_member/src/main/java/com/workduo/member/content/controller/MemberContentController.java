package com.workduo.member.content.controller;

import com.core.domain.memberContent.dto.ContentMemberUpdate;
import com.workduo.member.content.dto.*;
import com.workduo.member.content.service.MemberContentService;
import com.core.util.ApiUtils.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

import static com.core.util.ApiUtils.success;

;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/member/content")
@Tag(name="멤버 피드 서비스",description = "피드 등록,수정,삭제,코멘트 등록,삭제,좋아요 관련 API 입니다.")
public class MemberContentController {

    private final MemberContentService memberContentService;

    // 피드 생성
    @PostMapping(value = "",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "피드 생성 이 가능합니다." ,description = "피드 생성이 가능합니다.")
    public ApiResult<?> apiCreateContent(
            @RequestParam @Parameter(description = "multipart/form-data 형식의 이미지 리스트를 input 으로 받습니다장 최대 5장") List<MultipartFile> multipartFiles,
            @Validated @ParameterObject ContentCreate.Request req
    ) throws Exception {

        // 추후 업데이트 가 필요
        if (multipartFiles != null && multipartFiles.size() > 5) {
            throw new RuntimeException("사진은 최대 5장까지 업로드 가능합니다.");
        }

        memberContentService.createContent(req,multipartFiles);
        return success(null);
    }

    // 피드 리스트
    @GetMapping("/list")
    @Operation(summary = "피드 리스트 조회" ,description = "피드 리스트 조회 가 가능합니다.")
    public ApiResult<?> getContents(
            @ParameterObject Pageable pageable){
        Page<MemberContentListDto> contentList = memberContentService.getContentList(pageable);
        return success(contentList);
    }

    // 피드 상세
    @GetMapping("{memberContentId}")
    @Operation(summary = "피드 상세 조회" ,description = "피드 상세 조회 가 가능합니다.")
    public ApiResult<?> getSpecificContent(
            @PathVariable("memberContentId") Long memberContentId){
        MemberContentDetailDto contentDetail = memberContentService.getContentDetail(memberContentId);
        return success(contentDetail);
    }

    //피드 수정
    @PatchMapping("{memberContentId}")
    @Operation(summary = "피드 수정" ,description = "피드 수정 이 가능합니다.")
    public ApiResult<?> updateContent(
            @PathVariable("memberContentId") Long memberContentId,
            @RequestBody @Valid ContentMemberUpdate.Request req){
        memberContentService.contentUpdate(memberContentId,req);
        return success(null);
    }

    // 피드삭제
    @DeleteMapping("{memberContentId}")
    @Operation(summary = "피드 삭제" ,description = "피드 삭제 가 가능합니다.")
    public ApiResult<?> deleteContent(
            @PathVariable("memberContentId") Long contentId
    ){
        memberContentService.contentDelete(contentId);
        return success(null);
    }

    // 피드 좋아요
    @PostMapping("{memberContentId}/like")
    @Operation(summary = "피드 좋아요" ,description = "피드 좋아요 가 가능합니다.")
    public ApiResult<?> contentLike(
            @PathVariable("memberContentId") Long contentId
    ){
        memberContentService.contentLike(contentId);
        return success(null);
    }

    // 피드 좋아요 취소
    @DeleteMapping("{memberContentId}/like")
    @Operation(summary = "피드 좋아요 취소" ,description = "피드 좋아요 취소 가 가능합니다.")
    public ApiResult<?> contentLikeCancel(
            @PathVariable("memberContentId") Long contentId
    ){
        memberContentService.contentLikeCancel(contentId);
        return success(null);
    }

    // 피드 컨탠트  코멘트 작성
    @PostMapping("{memberContentId}/comment")
    @Operation(summary = "피드 댓글 작성" ,description = "피드 댓글 작성 이 가능합니다.")
    public ApiResult<?> contentComment(
            @PathVariable("memberContentId") Long contentId,
            @RequestBody @Valid ContentCommentCreate.Request req){

        memberContentService.contentCommentCreate(req,contentId);
        return success(null);
    }

     // 피드 커맨트 리스트
    @GetMapping("{memberContentId}/comment")
    @Operation(summary = "피드 댓글 리스트" ,description = "피드 댓글 리스트 조회 가 가능합니다.")
    public ApiResult<?> getCommentList(
            @ParameterObject Pageable pageable,
            @PathVariable("memberContentId") Long memberContentId){

        return success(memberContentService
                .getContentCommentList(memberContentId, pageable));
    }

    // 피드 댓글 업데이트
    @PatchMapping("{memberContentId}/comment/{commentId}")
    @Operation(summary = "피드 댓글 수정" ,description = "피드 댓글 수정 이 가능합니다.")
    public ApiResult<?> getContentComment(
            @PathVariable("memberContentId") Long memberContentId,
            @PathVariable("commentId") Long commentId,
            @RequestBody ContentCommentUpdate.Request req
    ){

        memberContentService.contentCommentUpdate(memberContentId,commentId,req);

        return success(null);
    }

    //피드 댓글 삭제
    @DeleteMapping("{memberContentId}/comment/{commentId}")
    @Operation(summary = "피드 댓글 삭제" ,description = "피드 댓글 삭제 가 가능합니다.")
    public ApiResult<?> deleteContentComment(
            @PathVariable("memberContentId") Long memberContentId,
            @PathVariable("commentId") Long commentId
    ){
        memberContentService.contentCommentDelete(memberContentId,commentId);
        return success(null);
    }
    //피드 댓글 좋아요
    @PostMapping("{memberContentId}/comment/{commentId}/like")
    @Operation(summary = "피드 댓글 좋아요" ,description = "피드 댓글 좋아요 가 가능합니다.")
    public ApiResult<?> contentCommentLike(
            @PathVariable("memberContentId") Long contentId,
            @PathVariable("commentId") Long commentId
    ){
        memberContentService.contentCommentLike(contentId,commentId);
        return success(null);
    }
    //피드 댓글 좋아요 취소
    @DeleteMapping("{memberContentId}/comment/{commentId}/like")
    @Operation(summary = "피드 댓글 좋아요 취소" ,description = "피드 댓글 좋아요 취소 가 가능합니다.")
    public ApiResult<?> contentCommentLikeCancel(
            @PathVariable("memberContentId") Long contentId,
            @PathVariable("commentId") Long commentId
    ){
        memberContentService.contentCommentLikeCancel(contentId,commentId);
        return success(null);
    }
}
