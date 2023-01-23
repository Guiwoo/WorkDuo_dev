package com.workduo.member.membercalendar.service;

import com.workduo.member.membercalendar.dto.CalendarDay;

import java.time.LocalDate;
import java.util.List;

public interface MemberCalendarService {
    List<String> getMonthCalendar(LocalDate date);

    List<CalendarDay> getDayCalendar(LocalDate date);
}
