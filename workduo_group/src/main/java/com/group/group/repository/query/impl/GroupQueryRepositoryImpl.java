package com.group.group.repository.query.impl;

import com.core.domain.area.sidoArea.QSidoArea;
import com.core.domain.area.siggArea.QSiggArea;
import com.core.domain.group.dto.GroupDto;
import com.core.domain.group.dto.GroupParticipantsDto;
import com.core.domain.group.dto.QGroupDto;
import com.core.domain.group.dto.QGroupParticipantsDto;
import com.core.domain.group.entity.QGroup;
import com.core.domain.group.entity.QGroupJoinMember;
import com.core.domain.group.entity.QGroupLike;
import com.core.domain.group.type.GroupJoinMemberStatus;
import com.core.domain.group.type.GroupRole;
import com.core.domain.group.type.GroupStatus;
import com.core.domain.member.entity.QMember;
import com.core.domain.member.entity.QMemberActiveArea;
import com.core.domain.sport.sport.QSport;
import com.core.domain.sport.sportCategory.QSportCategory;
import com.group.group.dto.ListGroup;
import com.group.group.repository.query.GroupQueryRepository;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.querydsl.core.types.Order.ASC;
import static com.querydsl.core.types.Order.DESC;
import static com.querydsl.jpa.JPAExpressions.select;


@Repository
@RequiredArgsConstructor
public class GroupQueryRepositoryImpl implements GroupQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<GroupDto> findById(Long groupId) {
        return Optional.ofNullable(jpaQueryFactory
                        .select(
                                new QGroupDto(
                                        QGroup.group.id.as("groupId"),
                                        QGroup.group.name,
                                        QGroup.group.limitPerson,
                                        QSiggArea.siggArea,
                                        QSidoArea.sidoArea,
                                        QSport.sport,
                                        QSportCategory.sportCategory,
                                        QGroup.group.introduce,
                                        QGroup.group.thumbnailPath,
                                        JPAExpressions.select(QGroupJoinMember.groupJoinMember.count())
                                                .from(QGroupJoinMember.groupJoinMember)
                                                .where(QGroupJoinMember.groupJoinMember.group.eq(QGroup.group)),
                                        JPAExpressions.select(QGroupLike.groupLike.count())
                                                .from(QGroupLike.groupLike)
                                                .where(QGroupLike.groupLike.group.eq(QGroup.group))
                                )
                        ).from(QGroup.group)
                        .join(QGroup.group.siggArea, QSiggArea.siggArea)
                        .join(QSiggArea.siggArea.sidoArea, QSidoArea.sidoArea)
                        .join(QGroup.group.sport, QSport.sport)
                        .join(QSport.sport.sportCategory, QSportCategory.sportCategory)
                        .where(QGroup.group.id.eq(groupId))
                        .fetchOne());
    }

    @Override
    public Page<GroupDto> findByGroupList(
            Pageable pageable,
            Long memberId,
            ListGroup.Request condition) {

        List<GroupDto> content = jpaQueryFactory
                .select(
                        new QGroupDto(
                                QGroup.group.id.as("groupId"),
                                QGroup.group.name,
                                QGroup.group.limitPerson,
                                QSiggArea.siggArea,
                                QSidoArea.sidoArea,
                                QSport.sport,
                                QSportCategory.sportCategory,
                                QGroup.group.introduce,
                                QGroup.group.thumbnailPath,
                                Expressions.as(
                                        JPAExpressions.select(QGroupJoinMember.groupJoinMember.count())
                                        .from(QGroupJoinMember.groupJoinMember)
                                        .where(QGroupJoinMember.groupJoinMember.group.eq(QGroup.group))
                                        , "participants"),
                                Expressions.as(
                                        JPAExpressions.select(QGroupLike.groupLike.count())
                                        .from(QGroupLike.groupLike)
                                        .where(QGroupLike.groupLike.group.eq(QGroup.group))
                                        , "likes")
                        )
                ).from(QGroup.group)
                .join(QGroup.group.siggArea, QSiggArea.siggArea)
                .join(QSiggArea.siggArea.sidoArea, QSidoArea.sidoArea)
                .join(QGroup.group.sport, QSport.sport)
                .join(QSport.sport.sportCategory, QSportCategory.sportCategory)
                .where(
                        groupStatusIng(),
                        siggAreaEq(condition.getSgg(), memberId),
                        sportEq(condition.getSportId())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(findByGroupListSort(pageable)
                        .stream().toArray(OrderSpecifier[]::new))
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(QGroup.group.count())
                .from(QGroup.group)
                .where(
                        groupStatusIng(),
                        siggAreaEq(condition.getSgg(), memberId),
                        sportEq(condition.getSportId())
                );

        return PageableExecutionUtils.getPage(
                content,
                pageable,
                countQuery::fetchOne
        );
    }

    @Override
    public Page<GroupParticipantsDto> findByGroupParticipantList(
            Pageable pageable,
            Long groupId) {

        List<GroupParticipantsDto> content = jpaQueryFactory
                .select(
                    new QGroupParticipantsDto(
                            QMember.member,
                            QGroupJoinMember.groupJoinMember
                    )
                ).from(QGroupJoinMember.groupJoinMember)
                .join(QGroupJoinMember.groupJoinMember.member, QMember.member)
                .where(
                        groupJoinMemberStatusIng(),
                        groupJoinMemberEqGroupId(groupId)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(findByGroupParticipantListSort())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(QGroupJoinMember.groupJoinMember.count())
                .from(QGroupJoinMember.groupJoinMember)
                .where(
                        groupJoinMemberStatusIng(),
                        groupJoinMemberEqGroupId(groupId)
                );

        return PageableExecutionUtils.getPage(
                content,
                pageable,
                countQuery::fetchOne
        );
    }

    private BooleanExpression groupStatusIng() {
        return QGroup.group.groupStatus.eq(GroupStatus.GROUP_STATUS_ING);
    }

    private BooleanExpression siggAreaEq(String sgg, Long memberId) {
        if (sgg == null && memberId == null) {
            return null;
        } else if (sgg == null && memberId != null) {
            return QGroup.group.siggArea.in(
                    select(QMemberActiveArea.memberActiveArea.siggArea)
                            .from(QMemberActiveArea.memberActiveArea)
                            .join(QMemberActiveArea.memberActiveArea.siggArea, QSiggArea.siggArea)
                            .where(QMemberActiveArea.memberActiveArea.member.id.eq(memberId))
            );
        } else {
            return QSiggArea.siggArea.sgg.eq(sgg);
        }
    }

    private BooleanExpression sportEq(Integer sportId) {
       return sportId == null ? null : QSport.sport.id.eq(sportId);
    }

    private List<OrderSpecifier> findByGroupListSort(Pageable pageable) {
        List<OrderSpecifier> orders = new ArrayList<>();
        StringPath aliasLikes = Expressions.stringPath("likes");
        if (!pageable.getSort().isEmpty()) {
            for (Sort.Order order : pageable.getSort()) {
                Order direction =
                        order.getDirection().isAscending() ? ASC : DESC;
                switch (order.getProperty()) {
                    case "likes" :
                        orders.add(new OrderSpecifier<>(direction, aliasLikes));
                        orders.add(new OrderSpecifier<>(DESC, QGroup.group.createdAt));
                        break;
                    case "date" :
                        orders.add(new OrderSpecifier<>(direction, QGroup.group.createdAt));
                        break;
                    default:
                        break;
                }
            }
        } else {
            orders.add(new OrderSpecifier<>(DESC, QGroup.group.createdAt));
        }

        return orders;
    }

    private BooleanExpression groupJoinMemberStatusIng() {
        return QGroupJoinMember.groupJoinMember.groupJoinMemberStatus.eq(GroupJoinMemberStatus.GROUP_JOIN_MEMBER_STATUS_ING);
    }

    private BooleanExpression groupJoinMemberEqGroupId(Long groupId) {
        return QGroupJoinMember.groupJoinMember.group.id.eq(groupId);
    }

    private OrderSpecifier<Integer> findByGroupParticipantListSort() {
        return new CaseBuilder()
                .when(QGroupJoinMember.groupJoinMember.groupRole.eq(GroupRole.GROUP_ROLE_LEADER)).then(1)
                .when(QGroupJoinMember.groupJoinMember.groupRole.eq(GroupRole.GROUP_ROLE_STAFF)).then(2)
                .otherwise(3).asc();
    }
}
