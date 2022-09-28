package com.workduo.group.groupmetting.service;

import com.workduo.group.groupmetting.dto.CreateMeeting;
import com.workduo.group.groupmetting.dto.MeetingDto;
import com.workduo.group.groupmetting.dto.TimeDto;
import com.workduo.group.groupmetting.dto.UpdateMeeting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface GroupMeetingService {

    /**
     * 유저 모임 일정
     * @param startDate
     */
    TimeDto meetingInquire(LocalDate startDate);

    /**
     * 그룹 모임 생성
     * @param groupId
     */
    void createMeeting(CreateMeeting.Request request, Long groupId);

    /**
     * 그룹 모임 리스트
     * @param pageable
     * @param groupId
     * @return
     */
    Page<MeetingDto> groupMeetingList(Pageable pageable, Long groupId);

    /**
     * 그룹 모임 상세
     * @param groupId
     * @param meetingId
     * @return
     */
    MeetingDto groupMeetingDetail(Long groupId, Long meetingId);

    /**
     * 그룹 모임 수정
     * @param groupId
     * @param meetingId
     * @param request
     */
    void groupMeetingUpdate(Long groupId, Long meetingId, UpdateMeeting.Request request);

    /**
     * 그룹 모임 삭제
     * @param groupId
     * @param meetingId
     */
    void groupMeetingDelete(Long groupId, Long meetingId);
}
