package com.workduo.group.gropcontent.controller;

import com.workduo.group.gropcontent.dto.createGroupContentComment.CreateComment;
import com.workduo.group.gropcontent.dto.creategroupcontent.CreateGroupContent;
import com.workduo.group.gropcontent.dto.updategroupcontent.UpdateContent;
import com.workduo.group.gropcontent.dto.updategroupcontentcomment.UpdateComment;
import com.workduo.group.gropcontent.service.GroupContentService;
import com.workduo.util.ApiUtils.ApiResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class GroupContentController {

    private final GroupContentService groupContentService;

    /**
     * 그룹 피드 리스트
     * @param groupId
     * @param pageable
     * @return
     */
    @GetMapping("/{groupId}/content")
    public ApiResult<?> groupContentList(
            @PathVariable("groupId") Long groupId,
            Pageable pageable) {

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
    public ApiResult<?> createGroupContent(
            @PathVariable("groupId") Long groupId,
            List<MultipartFile> multipartFiles,
            @Validated CreateGroupContent.Request request) {

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
    public ApiResult<?> groupContentCommentList(
            @PathVariable("groupId") Long groupId,
            @PathVariable("contentId") Long contentId,
            Pageable pageable) {

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
    public ApiResult<?> groupContentCommentUnLike(
            @PathVariable("groupId") Long groupId,
            @PathVariable("contentId") Long contentId,
            @PathVariable("commentId") Long commentId) {

        groupContentService.groupContentCommentUnLike(groupId, contentId, commentId);

        return success(null);
    }
}
