package com.workduo.member.membercalendar.dto;

import com.core.domain.memberCalendar.dto.CalendarDayDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class CalendarDay {
    private Long groupMeetingId;
    private String groupMeetingTitle;
    private String startDate;
    private String endDate;

    public static List<CalendarDay> from(List<CalendarDayDto> res){
         return res.stream().map(
                 (i)->CalendarDay.builder()
                         .groupMeetingId(i.getGroupMeetingId())
                         .groupMeetingTitle(i.getGroupMeetingTitle())
                         .startDate(dateParseToString(i.getStartDate()))
                         .endDate(dateParseToString(i.getEndDate()))
                         .build()
         ).collect(Collectors.toList());
    }

    private static String dateParseToString(LocalDateTime time){
        // 정책 에 따라 파싱 하는 방법이 달라져야 한다.
        int hour = time.getHour();
        int minute = time.getMinute();
        if(minute > 0) hour++;

        StringBuilder sb = new StringBuilder();

        if(hour < 10){
            sb.append("0").append(hour);
        }else{
            sb.append(hour);
        }

        sb.append(":").append("00");

        return sb.toString();
    }
}
