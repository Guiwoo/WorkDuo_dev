package com.workduo.member.membercalendar.service;

import com.workduo.common.CommonRequestContext;
import com.workduo.error.member.exception.MemberException;
import com.workduo.member.member.entity.Member;
import com.workduo.member.member.repository.MemberRepository;
import com.workduo.member.member.type.MemberStatus;
import com.workduo.member.membercalendar.dto.CalendarDay;
import com.workduo.member.membercalendar.dto.CalendarDayDto;
import com.workduo.member.membercalendar.repository.query.MemberCalendarQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static com.workduo.error.member.type.MemberErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberCalendarServiceImpl implements MemberCalendarService{

    private final MemberRepository memberRepository;
    private final CommonRequestContext commonRequestContext;
    private final MemberCalendarQueryRepository memberCalendarQueryRepository;
    @Override
    public List<String> getMonthCalendar(LocalDate date) {
        Member m = getMember();

        LocalDate start = YearMonth.from(date).atDay(1);
        LocalDateTime startDay = start.atStartOfDay();

        LocalDate end = YearMonth.from(date).atEndOfMonth();
        LocalDateTime endDay = end.atTime(23,59,59,999999);

        return memberCalendarQueryRepository.searchMemberMonthDate(m.getId(), startDay, endDay);
    }

    @Override
    public List<CalendarDay> getDayCalendar(LocalDate date) {
        Member m = getMember();

        LocalDateTime start = date.atTime(0,0);
        LocalDateTime end = date.atTime(23,59,59);

        List<CalendarDayDto> calendarDayDtos =
                memberCalendarQueryRepository.searchMemberDayDate(m.getId(), start, end);

        if(calendarDayDtos.size() == 0){
            throw new MemberException(MEMBER_CALENDAR_DOES_NOT_EXIST);
        }

        return CalendarDay.from(calendarDayDtos);
    }

    public Member getMember(){
        Member m = memberRepository.findByEmail(commonRequestContext.getMemberEmail())
                .orElseThrow(()-> new MemberException(MEMBER_EMAIL_ERROR));
        if(m.getMemberStatus() == MemberStatus.MEMBER_STATUS_STOP){
            throw new MemberException(MEMBER_STOP_ERROR);
        }
        if(m.getMemberStatus() == MemberStatus.MEMBER_STATUS_WITHDRAW){
            throw new MemberException(MEMBER_WITHDRAW_ERROR);
        }
        return m;
    }
}
