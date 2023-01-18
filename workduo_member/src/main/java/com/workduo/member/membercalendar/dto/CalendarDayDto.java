package com.workduo.member.membercalendar.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class CalendarDayDto {
    private Long groupMeetingId;
    private String groupMeetingTitle;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @QueryProjection
    public CalendarDayDto(Long groupMeetingId, String groupMeetingTitle, LocalDateTime startDate, LocalDateTime endDate) {
        this.groupMeetingId = groupMeetingId;
        this.groupMeetingTitle = groupMeetingTitle;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
