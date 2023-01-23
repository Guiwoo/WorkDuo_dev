package com.workduo.member.membercalendar.repository.query;

import com.core.domain.memberCalendar.dto.CalendarDayDto;
import com.core.domain.memberCalendar.dto.QCalendarDayDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.core.domain.groupMeeting.entity.QGroupMeeting.groupMeeting;
import static com.core.domain.memberCalendar.entity.QMemberCalendar.memberCalendar;
import static com.core.domain.memberCalendar.type.MeetingActiveStatus.MEETING_ACTIVE_STATUS_ING;

@Repository
@RequiredArgsConstructor
public class MemberCalendarQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public List<String> searchMemberMonthDate(Long memberId, LocalDateTime startDate,LocalDateTime endDate){
        StringPath aliasDate = Expressions.stringPath("day");
        List<String> fetch = jpaQueryFactory.select(Expressions.stringTemplate(
                        "DATE_FORMAT({0},{1})",
                        groupMeeting.meetingStartDate,
                        "%Y-%m-%d"
                ).as("day"))
                .from(memberCalendar)
                .join(memberCalendar.groupMeeting, groupMeeting)
                .where(
                        validateMemberCalendar(memberId,startDate,endDate)
                )
                .groupBy(aliasDate)
                .orderBy(aliasDate.asc())
                .fetch();

        return fetch;
    }

    public List<CalendarDayDto> searchMemberDayDate(Long memberId, LocalDateTime startDate, LocalDateTime endDate){

        return jpaQueryFactory.select(
                        new QCalendarDayDto(
                                groupMeeting.id,
                                groupMeeting.title,
                                groupMeeting.meetingStartDate,
                                groupMeeting.meetingEndDate
                        )
                )
                .from(memberCalendar)
                .join(memberCalendar.groupMeeting, groupMeeting)
                .where(
                        validateMemberCalendar(memberId,startDate,endDate)
                )
                .orderBy(groupMeeting.meetingStartDate.asc())
                .fetch();
    }

    private BooleanExpression validateMemberCalendar(Long id,LocalDateTime start,LocalDateTime end){
        return memberIdEq(id).and(meetingActiveStatus().and(betweenDate(start,end)));
    }
    private BooleanExpression memberIdEq(Long id){
        return memberCalendar.member.id.eq(id);
    }
    private BooleanExpression meetingActiveStatus(){
        return memberCalendar.meetingActiveStatus.eq(MEETING_ACTIVE_STATUS_ING);
    }
    private BooleanExpression betweenDate(LocalDateTime start,LocalDateTime end){
        return memberCalendar.groupMeeting.meetingStartDate.between(start, end);
    }
}
