package com.group.groupmetting.controller;

import com.core.util.ApiUtils;
import com.core.util.ApiUtils.ApiResult;
import com.group.config.aop.groupmeeting.GroupMeetingLock;
import com.group.groupmetting.dto.CreateMeeting;
import com.group.groupmetting.dto.UpdateMeeting;
import com.group.groupmetting.service.GroupMeetingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/v1/group")
@RequiredArgsConstructor
@Tag(name="그룹 미팅 서비스",description = "그룹 미팅 생성,삭제,조인,조회 등 관련 API 입니다.")
public class GroupMeetingController {

    private final GroupMeetingService groupMeetingService;

    /**
     * 유저 모임 일정
     * @param startDate
     * @return
     */
    @GetMapping("/meeting/inquire")
    @Operation(summary = "유저 모임 조회" ,description = "유저 모임 조회")
    public ApiResult<?> meetingInquire(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "일정 조회 시 YYYY-MM-DD 의 입력 이 필요합니다."
                    ,example = "2022-10-03")
            LocalDate startDate) {

        return ApiUtils.success(
                groupMeetingService.meetingInquire(startDate)
        );
    }

    /**
     * 그룹 모임 생성
     * @param groupId
     * @return
     */
    @PostMapping("/{groupId}/meeting")
    @Operation(summary = "그룹 모임 생성" ,description = "그룹 모임 생성")
    public ApiResult<?> createMeeting(
            @PathVariable("groupId") Long groupId,
            @RequestBody @Validated CreateMeeting.Request request) {

        groupMeetingService.createMeeting(request, groupId);
        return ApiUtils.success(null);
    }

    /**
     * 그룹 모임 리스트
     * @param groupId
     * @param pageable
     * @return
     */
    @GetMapping("/{groupId}/meeting")
    @Operation(summary = "그룹 모임 리스트 조회" ,description = "그룹 모임 리스트 조회, 그룹 모임 의 생성 최신순 으로 조회 됩니다.")
    public ApiResult<?> meetingList(
            @PathVariable("groupId") Long groupId,
            @ParameterObject Pageable pageable) {

        return ApiUtils.success(groupMeetingService.groupMeetingList(pageable, groupId));
    }

    /**
     * 그룹 모임 상세
     * @param groupId
     * @param meetingId
     * @return
     */
    @GetMapping("/{groupId}/meeting/{meetingId}")
    @Operation(summary = "그룹 모임 상세 조회" ,description = "그룹 모임 상세 조회")
    public ApiResult<?> getMeeting(
            @PathVariable("groupId") Long groupId,
            @PathVariable("meetingId") Long meetingId) {

        return ApiUtils.success(groupMeetingService.groupMeetingDetail(groupId, meetingId));
    }

    /**
     * 그룹 모임 수정
     * @param groupId
     * @param meetingId
     * @return
     */
    @PatchMapping("/{groupId}/meeting/{meetingId}")
    @Operation(summary = "그룹 모임 수정" ,description = "그룹 모임 수정")
    public ApiResult<?> updateMeeting(
            @PathVariable("groupId") Long groupId,
            @PathVariable("meetingId") Long meetingId,
            @RequestBody @Validated UpdateMeeting.Request request) {

        groupMeetingService.groupMeetingUpdate(groupId, meetingId, request);
        return ApiUtils.success(null);
    }

    /**
     * 그룹 모임 삭제
     * @param groupId
     * @param meetingId
     * @return
     */
    @DeleteMapping("/{groupId}/meeting/{meetingId}")
    @Operation(summary = "그룹 모임 삭제" ,description = "그룹 모임 삭제")
    public ApiResult<?> deleteMeeting(
            @PathVariable("groupId") Long groupId,
            @PathVariable("meetingId") Long meetingId) {

        groupMeetingService.groupMeetingDelete(groupId, meetingId);
        return ApiUtils.success(null);
    }

    /**
     * 그룹 모임 참여
     * @return
     */
    @PostMapping("/{groupId}/meeting/{meetingId}/participant")
    @Operation(summary = "그룹 모임 참여" ,description = "그룹 모임 참여")
    @GroupMeetingLock(tryLockTime = 3000L)
    public ApiResult<?> participantMeeting(
            @PathVariable("groupId") Long groupId,
            @PathVariable("meetingId") Long meetingId) {
        groupMeetingService.groupMeetingParticipant(groupId, meetingId);
        return ApiUtils.success(null);
    }

    /**
     * 그룹 모임 참여 취소
     * @param groupId
     * @param meetingId
     * @return
     */
    @DeleteMapping("/{groupId}/meeting/{meetingId}/participant")
    @Operation(summary = "그룹 모임 참여 취소" ,description = "그룹 모임 참여 취소")
    public ApiResult<?> cancelParticipantMeeting(
            @PathVariable("groupId") Long groupId,
            @PathVariable("meetingId") Long meetingId) {

        groupMeetingService.groupMeetingCancelParticipant(groupId, meetingId);
        return ApiUtils.success(null);
    }

    /**
     * 그룹 모임 참여자 리스트
     * @param groupId
     * @param meetingId
     * @param pageable
     * @return
     */
    @GetMapping("/{groupId}/meeting/{meetingId}/participant")
    @Operation(summary = "그룹 모임 참여자 리스트 조회" ,description = "그룹 모임 참여자 리스트 조회")
    public ApiResult<?> participantMeetingList(
            @PathVariable("groupId") Long groupId,
            @PathVariable("meetingId") Long meetingId,
            @ParameterObject Pageable pageable) {

        return ApiUtils.success(
                groupMeetingService.groupMeetingParticipantList(
                        pageable,
                        groupId,
                        meetingId
                )
        );
    }
}
