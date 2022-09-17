package com.workduo.group.gropcontent.controller;

import com.workduo.common.CommonResponse;
import com.workduo.error.global.exception.CustomMethodArgumentNotValidException;
import com.workduo.group.gropcontent.dto.creategroupcontent.CreateGroupContent;
import com.workduo.group.gropcontent.dto.detailgroupcontent.DetailGroupContent;
import com.workduo.group.gropcontent.dto.listgroupcontent.ListGroupContent;
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
}
