package com.workduo.group.gropcontent.controller;

import com.workduo.common.CommonResponse;
import com.workduo.error.global.exception.CustomMethodArgumentNotValidException;
import com.workduo.group.gropcontent.dto.createGroupContentComment.CreateComment;
import com.workduo.group.gropcontent.dto.creategroupcontent.CreateGroupContent;
import com.workduo.group.gropcontent.dto.detailgroupcontent.DetailContentComment;
import com.workduo.group.gropcontent.dto.detailgroupcontent.DetailGroupContent;
import com.workduo.group.gropcontent.dto.listgroupcontent.ListGroupContent;
import com.workduo.group.gropcontent.dto.updategroupcontent.UpdateContent;
import com.workduo.group.gropcontent.dto.updategroupcontentcomment.UpdateComment;
import com.workduo.group.gropcontent.service.GroupContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    public ResponseEntity<?> groupContentList(
            @PathVariable("groupId") Long groupId,
            Pageable pageable) {

        return new ResponseEntity<>(
                ListGroupContent.Response.from(
                        groupContentService.groupContentList(pageable, groupId)
                ),
                HttpStatus.OK
        );
    }

    /**
     * 그룹 피드 생성
     * @param groupId
     * @param request
     * @param bindingResult
     * @return
     */
    @PostMapping("/{groupId}/content")
    public ResponseEntity<?> createGroupContent(
            @PathVariable("groupId") Long groupId,
            List<MultipartFile> multipartFiles,
            @Validated CreateGroupContent.Request request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new CustomMethodArgumentNotValidException(bindingResult);
        }

        if (multipartFiles != null && multipartFiles.size() > 5) {
            throw new RuntimeException("사진은 최대 5장까지 업로드 가능합니다.");
        }

        groupContentService.createGroupContent(groupId, request, multipartFiles);
        return new ResponseEntity<>(
                CommonResponse.from()
                ,HttpStatus.CREATED
        );
    }

    /**
     * 그룹 피드 상세
     * @param groupId
     * @param contentId
     * @return
     */
    @GetMapping("/{groupId}/content/{contentId}")
    public ResponseEntity<?> detailGroupContent(
            @PathVariable("groupId") Long groupId,
            @PathVariable("contentId") Long contentId) {

        return new ResponseEntity<>(
                DetailGroupContent.Response.from(
                        groupContentService.detailGroupContent(groupId, contentId)
                ),
                HttpStatus.OK
        );
    }

    /**
     * 그룹 피드 좋아요
     * @param groupId
     * @param contentId
     * @return
     */
    @PostMapping("/{groupId}/content/{contentId}/like")
    public ResponseEntity<?> groupContentLike(
            @PathVariable("groupId") Long groupId,
            @PathVariable("contentId") Long contentId) {

        groupContentService.groupContentLike(groupId, contentId);
        return new ResponseEntity<>(
                CommonResponse.from(),
                HttpStatus.OK
        );
    }

    /**
     * 그룹 피드 좋아요 취소
     * @param groupId
     * @param contentId
     * @return
     */
    @DeleteMapping("/{groupId}/content/{contentId}/like")
    public ResponseEntity<?> groupContentUnLike(
            @PathVariable("groupId") Long groupId,
            @PathVariable("contentId") Long contentId) {

        groupContentService.groupContentUnLike(groupId, contentId);
        return new ResponseEntity<>(
                CommonResponse.from(),
                HttpStatus.OK
        );
    }

    /**
     * 그룹 피드 삭제
     * @param groupId
     * @param contentId
     * @return
     */
    @DeleteMapping("/{groupId}/content/{contentId}")
    public ResponseEntity<?> groupContentDelete(
            @PathVariable("groupId") Long groupId,
            @PathVariable("contentId") Long contentId) {

        groupContentService.groupContentDelete(groupId, contentId);
        return new ResponseEntity<>(
                CommonResponse.from(),
                HttpStatus.OK
        );
    }

    /**
     * 그룹 피드 수정
     * @param groupId
     * @param contentId
     * @param request
     * @param bindingResult
     * @return
     */
    @PatchMapping("/{groupId}/content/{contentId}")
    public ResponseEntity<?> groupContentUpdate(
            @PathVariable("groupId") Long groupId,
            @PathVariable("contentId") Long contentId,
            @RequestBody @Validated UpdateContent.Request request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new CustomMethodArgumentNotValidException(bindingResult);
        }

        groupContentService.groupContentUpdate(request, groupId, contentId);
        return new ResponseEntity<>(
                CommonResponse.from(),
                HttpStatus.OK
        );
    }

    /**
     * 그룹 피드 댓글 리스트
     * @param groupId
     * @param contentId
     * @param pageable
     * @return
     */
    @GetMapping("/{groupId}/content/{contentId}/comment")
    public ResponseEntity<?> groupContentCommentList(
            @PathVariable("groupId") Long groupId,
            @PathVariable("contentId") Long contentId,
            Pageable pageable) {

        return new ResponseEntity<>(
                DetailContentComment.Response.from(
                        groupContentService.groupContentCommentList(
                                pageable,
                                groupId,
                                contentId
                        )
                ),
                HttpStatus.OK
        );
    }

    /**
     * 그룹 피드 댓글 작성
     * @param groupId
     * @param contentId
     * @param request
     * @param bindingResult
     * @return
     */
    @PostMapping("/{groupId}/content/{contentId}/comment")
    public ResponseEntity<?> createGroupContentComment(
            @PathVariable("groupId") Long groupId,
            @PathVariable("contentId") Long contentId,
            @RequestBody @Validated CreateComment.Request request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new CustomMethodArgumentNotValidException(bindingResult);
        }

        groupContentService.createGroupContentComment(request, groupId, contentId);
        return new ResponseEntity<>(CommonResponse.from(), HttpStatus.OK);
    }

    /**
     * 그룹 피드 댓글 수정
     * @param groupId
     * @param contentId
     * @param commentId
     * @param request
     * @param bindingResult
     * @return
     */
    @PatchMapping("/{groupId}/content/{contentId}/comment/{commentId}")
    public ResponseEntity<?> updateGroupContentComment(
            @PathVariable("groupId") Long groupId,
            @PathVariable("contentId") Long contentId,
            @PathVariable("commentId") Long commentId,
            @RequestBody @Validated UpdateComment.Request request,
            BindingResult bindingResult) {

            if (bindingResult.hasErrors()) {
                throw new CustomMethodArgumentNotValidException(bindingResult);
            }

            groupContentService.updateGroupContentComment(request, groupId, contentId, commentId);
            return new ResponseEntity<>(
                    CommonResponse.from(),
                    HttpStatus.OK
            );
    }

    /**
     * 그룹 피드 댓글 삭제
     * @param groupId
     * @param contentId
     * @param commentId
     * @return
     */
    @DeleteMapping("/{groupId}/content/{contentId}/comment/{commentId}")
    public ResponseEntity<?> deleteGroupContentComment(
            @PathVariable("groupId") Long groupId,
            @PathVariable("contentId") Long contentId,
            @PathVariable("commentId") Long commentId) {

        groupContentService.deleteGroupContentComment(groupId, contentId, commentId);
        return new ResponseEntity<>(
                CommonResponse.from(),
                HttpStatus.OK
        );
    }

    /**
     * 그룹 피드 댓글 좋아요
     * @param groupId
     * @param contentId
     * @param commentId
     * @return
     */
    @PostMapping("/{groupId}/content/{contentId}/comment/{commentId}/like")
    public ResponseEntity<?> groupContentCommentLike(
            @PathVariable("groupId") Long groupId,
            @PathVariable("contentId") Long contentId,
            @PathVariable("commentId") Long commentId) {

        groupContentService.groupContentCommentLike(groupId, contentId, commentId);

        return new ResponseEntity<>(
                CommonResponse.from(),
                HttpStatus.OK
        );
    }

    /**
     * 그룹 피드 댓글 좋아요 취소
     * @param groupId
     * @param contentId
     * @param commentId
     * @return
     */
    @DeleteMapping("/{groupId}/content/{contentId}/comment/{commentId}/like")
    public ResponseEntity<?> groupContentCommentUnLike(
            @PathVariable("groupId") Long groupId,
            @PathVariable("contentId") Long contentId,
            @PathVariable("commentId") Long commentId) {

        groupContentService.groupContentCommentUnLike(groupId, contentId, commentId);

        return new ResponseEntity<>(
                CommonResponse.from(),
                HttpStatus.OK
        );
    }
}
