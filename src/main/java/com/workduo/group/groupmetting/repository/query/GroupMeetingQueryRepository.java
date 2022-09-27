package com.workduo.group.groupmetting.repository.query;

import com.workduo.group.groupmetting.dto.MeetingInquireDto;

import java.time.LocalDateTime;
import java.util.List;

public interface GroupMeetingQueryRepository {

    List<MeetingInquireDto> meetingInquireList(
            Long memberId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    boolean existsByMeeting(
            Long memberId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            LocalDateTime meetingStartDate,
            LocalDateTime meetingEndDate);
}
