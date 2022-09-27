package com.workduo.group.groupmetting.repository.query.impl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.workduo.group.groupmetting.dto.MeetingInquireDto;
import com.workduo.group.groupmetting.dto.QMeetingInquireDto;
import com.workduo.group.groupmetting.repository.query.GroupMeetingQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.querydsl.jpa.JPAExpressions.select;
import static com.workduo.group.groupmetting.entity.QGroupMeeting.groupMeeting;
import static com.workduo.group.groupmetting.entity.QGroupMeetingParticipant.groupMeetingParticipant;
import static com.workduo.member.member.entity.QMember.member;

@Repository
@RequiredArgsConstructor
public class GroupMeetingQueryRepositoryImpl implements GroupMeetingQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<MeetingInquireDto> meetingInquireList(Long memberId, LocalDateTime startDate, LocalDateTime endDate) {

        return jpaQueryFactory
                .select(
                        new QMeetingInquireDto(
                                startTime(groupMeeting.meetingStartDate),
                                minuteDiff(
                                    groupMeeting.meetingStartDate,
                                    groupMeeting.meetingEndDate
                                )
                        )
                )
                .from(groupMeeting)
                .where(inquireMeeting(memberId, startDate, endDate))
                .fetch();
    }

    @Override
    public boolean existsByMeeting(
            Long memberId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            LocalDateTime meetingStartDate,
            LocalDateTime meetingEndDate) {
        return jpaQueryFactory
                .select(groupMeeting)
                .from(groupMeeting)
                .where(
                        inquireMeeting(memberId, startDate, endDate),
                        includeStartDate(meetingStartDate)
                                .or(includeEndDate(meetingEndDate))
                                .or(includeStartDateAndEndDate(meetingStartDate, meetingEndDate))
                                .or(overlapStartDateAndEndDate(meetingStartDate, meetingEndDate))
                )
                .fetchFirst() != null;
    }

    private StringTemplate startTime(Expression<? extends LocalDateTime> startDate) {
        return  Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})",
                groupMeeting.meetingStartDate,
                "%H:%i"
        );
    }

    private NumberExpression<Integer> minuteDiff(
            Expression<? extends LocalDateTime> startDate,
            Expression<? extends LocalDateTime> endDate) {
        return MathExpressions.round(
                Expressions.numberTemplate(
                        Integer.class,
                        "TIMESTAMPDIFF(MINUTE, {0}, {1})",
                        startDate,
                        endDate
                )
                ,-1
        );
    }

    private BooleanExpression inquireMeeting(
            Long memberId,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        return groupMeeting.id.in(
                select(groupMeetingParticipant.groupMeeting.id)
                        .from(groupMeetingParticipant)
                        .join(groupMeetingParticipant.member, member)
                        .where(
                                groupMeetingParticipant.member.id.eq(memberId),
                                groupMeeting.meetingStartDate.between(
                                        startDate,
                                        endDate
                                )
                        )
        );
    }

    private BooleanExpression includeStartDate(LocalDateTime startDate) {
        return groupMeeting.meetingStartDate.loe(startDate)
                .and(groupMeeting.meetingEndDate.gt(startDate));
    }

    private BooleanExpression includeEndDate(LocalDateTime endDate) {
        return groupMeeting.meetingStartDate.lt(endDate)
                .and(groupMeeting.meetingEndDate.goe(endDate));
    }

    private BooleanExpression includeStartDateAndEndDate(LocalDateTime startDate, LocalDateTime endDate) {
        return groupMeeting.meetingStartDate.loe(startDate)
                .and(groupMeeting.meetingEndDate.goe(endDate));
    }

    private BooleanExpression overlapStartDateAndEndDate(LocalDateTime startDate, LocalDateTime endDate) {
        return groupMeeting.meetingStartDate.goe(startDate)
                .and(groupMeeting.meetingEndDate.loe(endDate));
    }
}
