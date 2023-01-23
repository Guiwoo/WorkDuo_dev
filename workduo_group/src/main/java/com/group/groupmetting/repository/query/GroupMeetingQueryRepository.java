package com.group.groupmetting.repository.query;

import com.core.domain.groupMeeting.dto.MeetingDto;
import com.core.domain.groupMeeting.dto.MeetingInquireDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    Page<MeetingDto> groupMeetingList(Pageable pageable, Long groupId);
    Optional<MeetingDto> findByGroupMeeting(Long meetingId, Long groupId, Long memberId);
}
