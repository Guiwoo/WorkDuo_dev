package com.group.groupmetting.repository.query.impl;

import com.core.domain.group.entity.QGroup;
import com.core.domain.groupMeeting.dto.MeetingDto;
import com.core.domain.groupMeeting.dto.MeetingInquireDto;
import com.core.domain.groupMeeting.dto.QMeetingDto;
import com.core.domain.groupMeeting.dto.QMeetingInquireDto;
import com.core.domain.groupMeeting.entity.QGroupMeeting;
import com.core.domain.groupMeeting.entity.QGroupMeetingParticipant;
import com.core.domain.member.entity.QMember;
import com.group.groupmetting.repository.query.GroupMeetingQueryRepository;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.querydsl.jpa.JPAExpressions.select;

@Repository
@RequiredArgsConstructor
public class GroupMeetingQueryRepositoryImpl implements GroupMeetingQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<MeetingInquireDto> meetingInquireList(Long memberId, LocalDateTime startDate, LocalDateTime endDate) {

        return jpaQueryFactory
                .select(
                        new QMeetingInquireDto(
                                startTime(),
                                minuteDiff(
                                    QGroupMeeting.groupMeeting.meetingStartDate,
                                    QGroupMeeting.groupMeeting.meetingEndDate
                                )
                        )
                )
                .from(QGroupMeeting.groupMeeting)
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
                .select(QGroupMeeting.groupMeeting)
                .from(QGroupMeeting.groupMeeting)
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
                                QGroupMeeting.groupMeeting.id,
                                QGroup.group.id,
                                QGroupMeeting.groupMeeting.title,
                                QGroupMeeting.groupMeeting.content,
                                QGroupMeeting.groupMeeting.location,
                                QGroupMeeting.groupMeeting.maxParticipant,
                                JPAExpressions.select(QGroupMeetingParticipant.groupMeetingParticipant.count())
                                        .from(QGroupMeetingParticipant.groupMeetingParticipant)
                                        .where(QGroupMeetingParticipant.groupMeetingParticipant.groupMeeting.id.eq(QGroupMeeting.groupMeeting.id)),
                                Expressions.stringTemplate(
                                        "DATE_FORMAT({0}, {1})",
                                        QGroupMeeting.groupMeeting.createdAt,
                                        "%Y-%m-%d %H%:%i"
                                ),
                                Expressions.stringTemplate(
                                        "DATE_FORMAT({0}, {1})",
                                        QGroupMeeting.groupMeeting.meetingStartDate,
                                        "%Y-%m-%d %H%:%i"
                                )
                        )
                )
                .from(QGroupMeeting.groupMeeting)
                .join(QGroupMeeting.groupMeeting.group, QGroup.group)
                .where(
                        groupMeetingIsDelYn(),
                        groupIdEq(groupId)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(QGroupMeeting.groupMeeting.createdAt.desc())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(QGroupMeeting.groupMeeting.count())
                .from(QGroupMeeting.groupMeeting)
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
                                        QGroupMeeting.groupMeeting.id,
                                        QGroup.group.id,
                                        QGroupMeeting.groupMeeting.title,
                                        QGroupMeeting.groupMeeting.content,
                                        QGroupMeeting.groupMeeting.location,
                                        QGroupMeeting.groupMeeting.maxParticipant,
                                        JPAExpressions.select(QGroupMeetingParticipant.groupMeetingParticipant.count())
                                                .from(QGroupMeetingParticipant.groupMeetingParticipant)
                                                .where(QGroupMeetingParticipant.groupMeetingParticipant.groupMeeting.id.eq(QGroupMeeting.groupMeeting.id)),
                                        Expressions.stringTemplate(
                                                "DATE_FORMAT({0}, {1})",
                                                QGroupMeeting.groupMeeting.createdAt,
                                                "%Y-%m-%d %H%:%i"
                                        ),
                                        Expressions.stringTemplate(
                                                "DATE_FORMAT({0}, {1})",
                                                QGroupMeeting.groupMeeting.meetingStartDate,
                                                "%Y-%m-%d %H%:%i"
                                        )
                                )
                        )
                        .from(QGroupMeeting.groupMeeting)
                        .join(QGroupMeeting.groupMeeting.group, QGroup.group)
                        .where(
                                groupMeetingIsDelYn(),
                                groupIdEq(groupId),
                                groupMeetingIdEq(meetingId)
                        )
                        .fetchOne()
        );
    }

    private BooleanExpression groupMeetingIsDelYn() {
        return QGroupMeeting.groupMeeting.deletedYn.eq(false);
    }

    private BooleanExpression groupMeetingIdEq(Long meetingId) {
        return QGroupMeeting.groupMeeting.id.eq(meetingId);
    }

    private BooleanExpression groupIdEq(Long groupId) {
        return QGroupMeeting.groupMeeting.group.id.eq(groupId);
    }

    private StringTemplate startTime() {
        return  Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})",
                QGroupMeeting.groupMeeting.meetingStartDate,
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
        return QGroupMeeting.groupMeeting.id.in(
                select(QGroupMeetingParticipant.groupMeetingParticipant.groupMeeting.id)
                        .from(QGroupMeetingParticipant.groupMeetingParticipant)
                        .join(QGroupMeetingParticipant.groupMeetingParticipant.member, QMember.member)
                        .where(
                                QGroupMeetingParticipant.groupMeetingParticipant.member.id.eq(memberId),
                                QGroupMeeting.groupMeeting.meetingStartDate.between(
                                        startDate,
                                        endDate
                                )
                        )
        );
    }

    private BooleanExpression includeStartDate(LocalDateTime startDate) {
        return QGroupMeeting.groupMeeting.meetingStartDate.loe(startDate)
                .and(QGroupMeeting.groupMeeting.meetingEndDate.gt(startDate));
    }

    private BooleanExpression includeEndDate(LocalDateTime endDate) {
        return QGroupMeeting.groupMeeting.meetingStartDate.lt(endDate)
                .and(QGroupMeeting.groupMeeting.meetingEndDate.goe(endDate));
    }

    private BooleanExpression includeStartDateAndEndDate(LocalDateTime startDate, LocalDateTime endDate) {
        return QGroupMeeting.groupMeeting.meetingStartDate.loe(startDate)
                .and(QGroupMeeting.groupMeeting.meetingEndDate.goe(endDate));
    }

    private BooleanExpression overlapStartDateAndEndDate(LocalDateTime startDate, LocalDateTime endDate) {
        return QGroupMeeting.groupMeeting.meetingStartDate.goe(startDate)
                .and(QGroupMeeting.groupMeeting.meetingEndDate.loe(endDate));
    }
}
