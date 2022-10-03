package com.workduo.group.group.controller;

import com.workduo.group.group.dto.CreateGroup;
import com.workduo.group.group.dto.ListGroup;
import com.workduo.group.group.dto.UpdateGroup;
import com.workduo.group.group.service.GroupService;
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
public class GroupController {

    private final GroupService groupService;

    /**
     * 그룹 생성
     * @param request
     * @return
     */
    @PostMapping("")
    public ApiResult<?> createGroup(
            List<MultipartFile> multipartFiles,
            @Validated CreateGroup.Request request) {

        if (multipartFiles == null || multipartFiles.size() <= 0) {
            throw new RuntimeException("그룹 썸네일은 필수 입력 사항입니다.");
        }

        groupService.createGroup(request, multipartFiles);

        return success(null);
    }

    /**
     * 그룹 해지 - 그룹장만 가능
     * @param groupId
     * @return
     */
    @PostMapping("/{groupId}")
    public ApiResult<?> deleteGroup(
            @PathVariable("groupId") Long groupId) {
        groupService.deleteGroup(groupId);

        return success(null);
    }

    /**
     * 그룹 탈퇴 - 그룹장은 불가능
     * @param groupId
     * @return
     */
    @DeleteMapping("/{groupId}")
    public ApiResult<?> withdrawGroup(
            @PathVariable("groupId") Long groupId) {
        groupService.withdrawGroup(groupId);

        return success(null);
    }

    /**
     * 그룹 상세
     * @param groupId
     * @return
     */
    @GetMapping("/{groupId}")
    public ApiResult<?> detailGroup(
            @PathVariable("groupId") Long groupId) {

        return success(groupService.groupDetail(groupId));
    }

    /**
     * 그룹 리스트
     */
    @GetMapping("")
    public ApiResult<?> groupList(
            Pageable pageable,
            ListGroup.Request condition) {
        return success(groupService.groupList(pageable, condition));
    }

    /**
     * 그룹 좋아요
     * @param groupId
     * @return
     */
    @PostMapping("/{groupId}/like")
    public ApiResult<?> groupLike(
            @PathVariable("groupId") Long groupId) {

        groupService.groupLike(groupId);
        return success(null);
    }

    /**
     * 그룹 좋아요 취소
     * @param groupId
     * @return
     */
    @DeleteMapping("/{groupId}/like")
    public ApiResult<?> groupUnLike(
            @PathVariable("groupId") Long groupId) {

        groupService.groupUnLike(groupId);
        return success(null);
    }

    /**
     * 그룹 참여
     * @param groupId
     * @return
     */
    @PostMapping("/{groupId}/participant")
    public ApiResult<?> groupParticipant(
            @PathVariable("groupId") Long groupId) {

        groupService.groupParticipant(groupId);
        return success(null);
    }

    /**
     * 그룹 참여자 리스트
     * @param pageable
     * @return
     */
    @GetMapping("participant/{groupId}")
    public ApiResult<?> groupParticipantList(
            Pageable pageable,
            @PathVariable("groupId") Long groupId) {

        return success(
                groupService.groupParticipantList(pageable, groupId)
        );
    }

    /**
     * 그룹 썸네일 수정
     * @param groupId
     * @param multipartFiles
     * @return
     */
    @PatchMapping("/{groupId}/thumbnail")
    public ApiResult<?> groupThumbnailUpdate(
            @PathVariable("groupId") Long groupId,
            List<MultipartFile> multipartFiles) {

        if (multipartFiles == null || multipartFiles.size() <= 0) {
            throw new RuntimeException("그룹 썸네일은 필수 입력 사항입니다.");
        }

        return success(groupService.groupThumbnailUpdate(groupId, multipartFiles));
    }

    /**
     * 그룹 수정
     * @param groupId
     * @param request
     * @return
     */
    @PatchMapping("/{groupId}")
    public ApiResult<?> groupUpdate(
            @PathVariable("groupId") Long groupId,
            @RequestBody @Validated UpdateGroup.Request request) {

        return success(groupService.groupUpdate(groupId, request));
    }
}
