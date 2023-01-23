package com.core.domain.memberCalendar.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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
