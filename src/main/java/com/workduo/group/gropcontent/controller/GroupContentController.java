package com.workduo.group.gropcontent.controller;

import com.workduo.group.gropcontent.dto.createGroupContentComment.CreateComment;
import com.workduo.group.gropcontent.dto.creategroupcontent.CreateGroupContent;
import com.workduo.group.gropcontent.dto.updategroupcontent.UpdateContent;
import com.workduo.group.gropcontent.dto.updategroupcontentcomment.UpdateComment;
import com.workduo.group.gropcontent.service.GroupContentService;
import com.workduo.util.ApiUtils.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.workduo.util.ApiUtils.success;

@Slf4j
@RestController
@RequestMapping("/api/v1/group")
@RequiredArgsConstructor
@Tag(name="그룹 피드 서비스",description = "그룹 피드 생성,삭제,조인,조회 등 관련 API 입니다.")
public class GroupContentController {

    private final GroupContentService groupContentService;

    /**
     * 그룹 피드 리스트
     * @param groupId
     * @param pageable
     * @return
     */
    @GetMapping("/{groupId}/content")
    @Operation(summary = "그룹 피드 리스트 조회 가 가능합니다." ,description = "그룹 피드 리스트 가 조회 가능합니다.")
    public ApiResult<?> groupContentList(
            @PathVariable("groupId") Long groupId,
            @ParameterObject Pageable pageable)
    {
        return success(
                groupContentService.groupContentList(pageable, groupId)
        );
    }

    /**
     * 그룹 피드 생성
     * @param groupId
     * @param request
     * @return
     */
    @PostMapping("/{groupId}/content")
    @Operation(summary = "그룹 피드 생성 이 가능합니다." ,description = "그룹 피드 생성 이 가능합니다.")
    public ApiResult<?> createGroupContent(
            @PathVariable("groupId") Long groupId,
            @Parameter(description = "multipart/form-data 형식의 이미지 리스트를 input 으로 받습니다 1장") List<MultipartFile> multipartFiles,
            @Validated @ParameterObject CreateGroupContent.Request request) {

        if (multipartFiles != null && multipartFiles.size() > 5) {
            throw new RuntimeException("사진은 최대 5장까지 업로드 가능합니다.");
        }

        groupContentService.createGroupContent(groupId, request, multipartFiles);
        return success(null);
    }

    /**
     * 그룹 피드 상세
     * @param groupId
     * @param contentId
     * @return
     */
    @GetMapping("/{groupId}/content/{contentId}")
    @Operation(summary = "그룹 피드 상세조회 가 가능합니다." ,description = "그룹 피드 상세조회 가 가능합니다.")
    public ApiResult<?> detailGroupContent(
            @PathVariable("groupId") Long groupId,
            @PathVariable("contentId") Long contentId) {

        return success(
                groupContentService.detailGroupContent(groupId, contentId)
        );
    }

    /**
     * 그룹 피드 좋아요
     * @param groupId
     * @param contentId
     * @return
     */
    @PostMapping("/{groupId}/content/{contentId}/like")
    @Operation(summary = "그룹 피드 좋아요  가능합니다." ,description = "그룹 피드 좋아요 가 가능합니다.")
    public ApiResult<?> groupContentLike(
            @PathVariable("groupId") Long groupId,
            @PathVariable("contentId") Long contentId) {

        groupContentService.groupContentLike(groupId, contentId);
        return success(null);
    }

    /**
     * 그룹 피드 좋아요 취소
     * @param groupId
     * @param contentId
     * @return
     */
    @DeleteMapping("/{groupId}/content/{contentId}/like")
    @Operation(summary = "그룹 피드 좋아요 취소 가능합니다." ,description = "그룹 피드 좋아요 취소 가 가능합니다.")
    public ApiResult<?> groupContentUnLike(
            @PathVariable("groupId") Long groupId,
            @PathVariable("contentId") Long contentId) {

        groupContentService.groupContentUnLike(groupId, contentId);
        return success(null);
    }

    /**
     * 그룹 피드 삭제
     * @param groupId
     * @param contentId
     * @return
     */
    @DeleteMapping("/{groupId}/content/{contentId}")
    @Operation(summary = "그룹 피드 삭제 가 가능합니다." ,description = "그룹 피드 삭제 가 가능합니다.")
    public ApiResult<?> groupContentDelete(
            @PathVariable("groupId") Long groupId,
            @PathVariable("contentId") Long contentId) {

        groupContentService.groupContentDelete(groupId, contentId);
        return success(null);
    }

    /**
     * 그룹 피드 수정
     * @param groupId
     * @param contentId
     * @param request
     * @return
     */
    @PatchMapping("/{groupId}/content/{contentId}")
    @Operation(summary = "그룹 피드 수정 이 가능합니다." ,description = "그룹 피드 수정 이 가능합니다.")
    public ApiResult<?> groupContentUpdate(
            @PathVariable("groupId") Long groupId,
            @PathVariable("contentId") Long contentId,
            @RequestBody @Validated UpdateContent.Request request) {

        groupContentService.groupContentUpdate(request, groupId, contentId);
        return success(null);
    }

    /**
     * 그룹 피드 댓글 리스트
     * @param groupId
     * @param contentId
     * @param pageable
     * @return
     */
    @GetMapping("/{groupId}/content/{contentId}/comment")
    @Operation(summary = "그룹 피드 댓글 리스트 조회 가 가능합니다." ,description = "그룹 피드 댓글 리스트 조회 가 가능합니다. commentLike, 생성 일 순으로 조회 됩니다.")
    public ApiResult<?> groupContentCommentList(
            @PathVariable("groupId") Long groupId,
            @PathVariable("contentId") Long contentId,
            @ParameterObject Pageable pageable) {

        return success(
                groupContentService.groupContentCommentList(
                    pageable,
                    groupId,
                    contentId
                )
        );
    }

    /**
     * 그룹 피드 댓글 작성
     * @param groupId
     * @param contentId
     * @param request
     * @return
     */
    @PostMapping("/{groupId}/content/{contentId}/comment")
    @Operation(summary = "그룹 피드 댓글 작성 이 가능합니다."
            ,description = "그룹 피드 댓글 작성 이 가능합니다.")
    public ApiResult<?> createGroupContentComment(
            @PathVariable("groupId") Long groupId,
            @PathVariable("contentId") Long contentId,
            @RequestBody @Validated CreateComment.Request request) {

        groupContentService.createGroupContentComment(request, groupId, contentId);
        return success(null);
    }

    /**
     * 그룹 피드 댓글 수정
     * @param groupId
     * @param contentId
     * @param commentId
     * @param request
     * @return
     */
    @PatchMapping("/{groupId}/content/{contentId}/comment/{commentId}")
    @Operation(summary = "그룹 피드 댓글 수정 이 가능합니다."
            ,description = "그룹 피드 댓글 수정 이 가능합니다.")
    public ApiResult<?> updateGroupContentComment(
            @PathVariable("groupId") Long groupId,
            @PathVariable("contentId") Long contentId,
            @PathVariable("commentId") Long commentId,
            @RequestBody @Validated UpdateComment.Request request) {

            groupContentService.updateGroupContentComment(request, groupId, contentId, commentId);
            return success(null);
    }

    /**
     * 그룹 피드 댓글 삭제
     * @param groupId
     * @param contentId
     * @param commentId
     * @return
     */
    @DeleteMapping("/{groupId}/content/{contentId}/comment/{commentId}")
    @Operation(summary = "그룹 피드 댓글 삭제 가 가능합니다."
            ,description = "그룹 피드 댓글 삭제 가 가능합니다.")
    public ApiResult<?> deleteGroupContentComment(
            @PathVariable("groupId") Long groupId,
            @PathVariable("contentId") Long contentId,
            @PathVariable("commentId") Long commentId) {

        groupContentService.deleteGroupContentComment(groupId, contentId, commentId);
        return success(null);
    }

    /**
     * 그룹 피드 댓글 좋아요
     * @param groupId
     * @param contentId
     * @param commentId
     * @return
     */
    @PostMapping("/{groupId}/content/{contentId}/comment/{commentId}/like")
    @Operation(summary = "그룹 피드 댓글 좋아요 가 가능합니다."
            ,description = "그룹 피드 댓글 좋아요 가 가능합니다.")
    public ApiResult<?> groupContentCommentLike(
            @PathVariable("groupId") Long groupId,
            @PathVariable("contentId") Long contentId,
            @PathVariable("commentId") Long commentId) {

        groupContentService.groupContentCommentLike(groupId, contentId, commentId);

        return success(null);
    }

    /**
     * 그룹 피드 댓글 좋아요 취소
     * @param groupId
     * @param contentId
     * @param commentId
     * @return
     */
    @DeleteMapping("/{groupId}/content/{contentId}/comment/{commentId}/like")
    @Operation(summary = "그룹 피드 댓글 좋아요 취소 가 가능합니다."
            ,description = "그룹 피드 댓글 좋아요 취소소가 가능합니다.")
    public ApiResult<?> groupContentCommentUnLike(
            @PathVariable("groupId") Long groupId,
            @PathVariable("contentId") Long contentId,
            @PathVariable("commentId") Long commentId) {

        groupContentService.groupContentCommentUnLike(groupId, contentId, commentId);

        return success(null);
    }
}
