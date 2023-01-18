package com.workduo.group.group.controller;

import com.core.domain.group.dto.UpdateGroup;
import com.workduo.group.group.dto.CreateGroup;
import com.workduo.group.group.dto.ListGroup;
import com.workduo.group.group.service.GroupService;
import com.workduo.util.ApiUtils.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.workduo.util.ApiUtils.success;

@Slf4j
@RestController
@RequestMapping("/api/v1/group")
@RequiredArgsConstructor
@Tag(name="그룹 서비스",description = "그룹 생성,삭제,조인,조회 등 관련 API 입니다.")
public class GroupController {

    private final GroupService groupService;

    /**
     * 그룹 생성
     * @param request
     * @return
     */
    @PostMapping(value = "",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "그룹 생성 이 가능합니다." ,description = "그룹 생성이 가능합니다.")
    public ApiResult<?> createGroup(
            @Parameter(description = "multipart/form-data 형식의 이미지 리스트를 input 으로 받습니다 1장") List<MultipartFile> multipartFiles,
            @Validated @ParameterObject CreateGroup.Request request) {

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
    @Operation(summary = "그룹 해지 가 가능합니다." ,description = "오직 그룹장 만 이 가능합니다.")
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
    @Operation(summary = "그룹 탈퇴 가 가능합니다." ,description = "그룹장 을 제외한 그룹 가입 멤버 들 만 가능 합니다.")
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
    @Operation(summary = "그룹 상세 조회 가 가능합니다." ,description = "그룹 상세 조회 가 가능합니다.")
    public ApiResult<?> detailGroup(
            @PathVariable("groupId") Long groupId) {

        return success(groupService.groupDetail(groupId));
    }

    /**
     * 그룹 리스트
     */
    @GetMapping("")
    @Operation(summary = "그룹 리스트 조회 가 가능합니다." ,
            description = "그룹 리스트 조회 가 가능합니다. condition 이 없다면 최신 생성일 순 으로 조회합니다.")
    public ApiResult<?> groupList(
            @ParameterObject Pageable pageable,
            ListGroup.Request condition) {
        return success(groupService.groupList(pageable, condition));
    }

    /**
     * 그룹 좋아요
     * @param groupId
     * @return
     */
    @PostMapping("/{groupId}/like")
    @Operation(summary = "그룹 좋아요 가 가능합니다." ,description = "그룹 좋아요 가 가능합니다.")
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
    @Operation(summary = "그룹 좋아요 취소 가 가능합니다." ,description = "그룹 좋아요 취소 가 가능합니다.")
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
    @Operation(summary = "그룹 참여 가 가능합니다." ,description = "그룹 참여 가 가능합니다.")
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
    @Operation(summary = "그룹 참여자 리스트 조회 가능합니다." ,description = "그룹 참여자 리스트 조회 가 가능합니다.")
    public ApiResult<?> groupParticipantList(
            @ParameterObject Pageable pageable,
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
    @Operation(summary = "그룹 썸네일 수정" ,description = "그룹 썸네일 수정 이 가능합니다. 1장 의 이미지 가 필요합니다.")
    public ApiResult<?> groupThumbnailUpdate(
            @PathVariable("groupId") Long groupId,
            @Parameter(description = "multipart/form-data 형식의 이미지 리스트를 input 으로 받습니다 1장")
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
    @Operation(summary = "그룹 정보 수정" ,description = "그룹 정보 수정 이 가능 합니다.")
    public ApiResult<?> groupUpdate(
            @PathVariable("groupId") Long groupId,
            @RequestBody @Validated UpdateGroup.Request request) {

        return success(groupService.groupUpdate(groupId, request));
    }
}
