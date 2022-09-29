package com.workduo.group.groupmetting.controller;

import com.workduo.common.CommonResponse;
import com.workduo.configuration.aop.groupmeeting.GroupMeetingLock;
import com.workduo.error.global.exception.CustomMethodArgumentNotValidException;
import com.workduo.group.groupmetting.dto.CreateMeeting;
import com.workduo.group.groupmetting.dto.UpdateMeeting;
import com.workduo.group.groupmetting.service.GroupMeetingService;
import com.workduo.util.ApiUtils.ApiResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static com.workduo.util.ApiUtils.success;

@Slf4j
@RestController
@RequestMapping("/api/v1/group")
@RequiredArgsConstructor
public class GroupMeetingController {

    private final GroupMeetingService groupMeetingService;

    /**
     * 유저 모임 일정
     * @param startDate
     * @return
     */
    @GetMapping("/meeting/inquire")
    public ApiResult<?> meetingInquire(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {

        return success(
                groupMeetingService.meetingInquire(startDate)
        );
    }

    /**
     * 그룹 모임 생성
     * @param groupId
     * @return
     */
    @PostMapping("/{groupId}/meeting")
    public ApiResult<?> createMeeting(
            @PathVariable("groupId") Long groupId,
            @RequestBody @Validated CreateMeeting.Request request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new CustomMethodArgumentNotValidException(bindingResult);
        }

        groupMeetingService.createMeeting(request, groupId);
        return success(null);
    }

    /**
     * 그룹 모임 리스트
     * @param groupId
     * @param pageable
     * @return
     */
    @GetMapping("/{groupId}/meeting")
    public ApiResult<?> meetingList(
            @PathVariable("groupId") Long groupId,
            Pageable pageable) {

        return success(
                groupMeetingService.groupMeetingList(pageable, groupId)
        );
    }

    /**
     * 그룹 모임 상세
     * @param groupId
     * @param meetingId
     * @return
     */
    @GetMapping("/{groupId}/meeting/{meetingId}")
    public ApiResult<?> getMeeting(
            @PathVariable("groupId") Long groupId,
            @PathVariable("meetingId") Long meetingId) {

        return success(groupMeetingService.groupMeetingDetail(groupId, meetingId));
    }

    /**
     * 그룹 모임 수정
     * @param groupId
     * @param meetingId
     * @return
     */
    @PatchMapping("/{groupId}/meeting/{meetingId}")
    public ApiResult<?> updateMeeting(
            @PathVariable("groupId") Long groupId,
            @PathVariable("meetingId") Long meetingId,
            @RequestBody @Validated UpdateMeeting.Request request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new CustomMethodArgumentNotValidException(bindingResult);
        }

        groupMeetingService.groupMeetingUpdate(groupId, meetingId, request);
        return success(null);
    }

    /**
     * 그룹 모임 삭제
     * @param groupId
     * @param meetingId
     * @return
     */
    @DeleteMapping("/{groupId}/meeting/{meetingId}")
    public ApiResult<?> deleteMeeting(
            @PathVariable("groupId") Long groupId,
            @PathVariable("meetingId") Long meetingId) {

        groupMeetingService.groupMeetingDelete(groupId, meetingId);
        return success(null);
    }

    /**
     * 그룹 모임 참여
     * @return
     */
    @PostMapping("/{groupId}/meeting/{meetingId}/participant")
    @GroupMeetingLock(tryLockTime = 3000L)
    public ApiResult<?> participantMeeting(
            @PathVariable("groupId") Long groupId,
            @PathVariable("meetingId") Long meetingId) {
        groupMeetingService.groupMeetingParticipant(groupId, meetingId);
        return success(null);
    }

    /**
     * 그룹 모임 참여 취소
     * @param groupId
     * @param meetingId
     * @return
     */
    @DeleteMapping("/{groupId}/meeting/{meetingId}/participant")
    public ResponseEntity<?> cancelParticipantMeeting(
            @PathVariable("groupId") Long groupId,
            @PathVariable("meetingId") Long meetingId) {

        return null;
    }

    /**
     * 그룹 모임 참여자 리스트
     * @param groupId
     * @param meetingId
     * @param pageable
     * @return
     */
    @GetMapping("/{groupId}/meeting/{meetingId}/participant")
    public ResponseEntity<?> participantMeetingList(
            @PathVariable("groupId") Long groupId,
            @PathVariable("meetingId") Long meetingId,
            Pageable pageable) {

        return null;
    }
}
