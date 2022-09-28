package com.workduo.group.groupmetting.repository.query.impl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.workduo.group.group.entity.QGroup;
import com.workduo.group.groupmetting.dto.MeetingDto;
import com.workduo.group.groupmetting.dto.MeetingInquireDto;
import com.workduo.group.groupmetting.dto.QMeetingDto;
import com.workduo.group.groupmetting.dto.QMeetingInquireDto;
import com.workduo.group.groupmetting.repository.query.GroupMeetingQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.querydsl.jpa.JPAExpressions.select;
import static com.workduo.group.group.entity.QGroup.group;
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

    @Override
    public Page<MeetingDto> groupMeetingList(Pageable pageable, Long groupId) {

        List<MeetingDto> content = jpaQueryFactory
                .select(
                        new QMeetingDto(
                                groupMeeting.id,
                                group.id,
                                groupMeeting.title,
                                groupMeeting.content,
                                groupMeeting.location,
                                groupMeeting.maxParticipant,
                                select(groupMeetingParticipant.count())
                                        .from(groupMeetingParticipant)
                                        .where(groupMeetingParticipant.groupMeeting.id.eq(groupMeeting.id)),
                                Expressions.stringTemplate(
                                        "DATE_FORMAT({0}, {1})",
                                        groupMeeting.createdAt,
                                        "%Y-%m-%d %H%:%i"
                                ),
                                Expressions.stringTemplate(
                                        "DATE_FORMAT({0}, {1})",
                                        groupMeeting.meetingStartDate,
                                        "%Y-%m-%d %H%:%i"
                                )
                        )
                )
                .from(groupMeeting)
                .join(groupMeeting.group, group)
                .where(
                        groupMeetingIsDelYn(),
                        groupIdEq(groupId)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(groupMeeting.createdAt.desc())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(groupMeeting.count())
                .from(groupMeeting)
                .where(
                        groupMeetingIsDelYn(),
                        groupIdEq(groupId)
                );

        return PageableExecutionUtils.getPage(
                content,
                pageable,
                countQuery::fetchOne
        );
    }

    @Override
    public Optional<MeetingDto> findByGroupMeeting(Long meetingId, Long groupId, Long memberId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .select(
                                new QMeetingDto(
                                        groupMeeting.id,
                                        group.id,
                                        groupMeeting.title,
                                        groupMeeting.content,
                                        groupMeeting.location,
                                        groupMeeting.maxParticipant,
                                        select(groupMeetingParticipant.count())
                                                .from(groupMeetingParticipant)
                                                .where(groupMeetingParticipant.groupMeeting.id.eq(groupMeeting.id)),
                                        Expressions.stringTemplate(
                                                "DATE_FORMAT({0}, {1})",
                                                groupMeeting.createdAt,
                                                "%Y-%m-%d %H%:%i"
                                        ),
                                        Expressions.stringTemplate(
                                                "DATE_FORMAT({0}, {1})",
                                                groupMeeting.meetingStartDate,
                                                "%Y-%m-%d %H%:%i"
                                        )
                                )
                        )
                        .from(groupMeeting)
                        .join(groupMeeting.group, group)
                        .where(
                                groupMeetingIsDelYn(),
                                groupIdEq(groupId),
                                groupMeetingIdEq(meetingId),
                                groupMeetingInMemberIdEq(memberId)
                        )
                        .fetchOne()
        );
    }

    private BooleanExpression groupMeetingIsDelYn() {
        return groupMeeting.deletedYn.eq(false);
    }

    private BooleanExpression groupMeetingInMemberIdEq(Long memberId) {
        return memberId == null ? null : groupMeeting.member.id.eq(memberId);
    }

    private BooleanExpression groupMeetingIdEq(Long meetingId) {
        return groupMeeting.id.eq(meetingId);
    }

    private BooleanExpression groupIdEq(Long groupId) {
        return groupMeeting.group.id.eq(groupId);
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
