package com.workduo.group.group.repository.query.impl;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.workduo.group.group.dto.GroupDto;
import com.workduo.group.group.dto.ListGroup;
import com.workduo.group.group.dto.QGroupDto;
import com.workduo.group.group.repository.query.GroupQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.querydsl.jpa.JPAExpressions.select;
import static com.workduo.area.sidoarea.entity.QSidoArea.sidoArea;
import static com.workduo.area.siggarea.entity.QSiggArea.siggArea;
import static com.workduo.group.group.entity.QGroup.group;
import static com.workduo.group.group.entity.QGroupJoinMember.groupJoinMember;
import static com.workduo.group.group.entity.QGroupLike.groupLike;
import static com.workduo.group.group.type.GroupStatus.GROUP_STATUS_ING;
import static com.workduo.sport.sport.entity.QSport.sport;
import static com.workduo.sport.sportcategory.entity.QSportCategory.sportCategory;


@Repository
@RequiredArgsConstructor
public class GroupQueryRepositoryImpl implements GroupQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<GroupDto> findById(Long groupId) {
        return Optional.ofNullable(jpaQueryFactory
                        .select(
                                new QGroupDto(
                                        group.id.as("groupId"),
                                        group.name,
                                        group.limitPerson,
                                        siggArea,
                                        sidoArea,
                                        sport,
                                        sportCategory,
                                        group.introduce,
                                        group.thumbnailPath,
                                        select(groupJoinMember.count())
                                                .from(groupJoinMember)
                                                .where(groupJoinMember.group.eq(group)),
                                        select(groupLike.count())
                                                .from(groupLike)
                                                .where(groupLike.group.eq(group))
                                )
                        ).from(group)
                        .join(group.siggArea, siggArea)
                        .join(siggArea.sidoArea, sidoArea)
                        .join(group.sport, sport)
                        .join(sport.sportCategory, sportCategory)
                        .where(group.id.eq(groupId))
                        .fetchOne());
    }

    @Override
    public Page<GroupDto> findByGroupList(
            Pageable pageable,
            ListGroup.Request condition) {


        List<GroupDto> content = jpaQueryFactory
                .select(
                        new QGroupDto(
                                group.id.as("groupId"),
                                group.name,
                                group.limitPerson,
                                siggArea,
                                sidoArea,
                                sport,
                                sportCategory,
                                group.introduce,
                                group.thumbnailPath,
                                Expressions.as(
                                        select(groupJoinMember.count())
                                        .from(groupJoinMember)
                                        .where(groupJoinMember.group.eq(group))
                                        , "participants"),
                                Expressions.as(
                                        select(groupLike.count())
                                        .from(groupLike)
                                        .where(groupLike.group.eq(group))
                                        , "likes")
                        )
                ).from(group)
                .join(group.siggArea, siggArea)
                .join(siggArea.sidoArea, sidoArea)
                .join(group.sport, sport)
                .join(sport.sportCategory, sportCategory)
                .where(
                        groupStatusIng(),
                        siggAreaEq(condition.getSiggAreaId()),
                        sportEq(condition.getSportId())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(sort(pageable).stream().toArray(OrderSpecifier[]::new))
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(group.count())
                .from(group)
                .where(
                        groupStatusIng(),
                        siggAreaEq(condition.getSiggAreaId()),
                        sportEq(condition.getSportId())
                );

        return PageableExecutionUtils.getPage(
                content,
                pageable,
                countQuery::fetchOne);
    }

    private BooleanExpression groupStatusIng() {
        return group.groupStatus.eq(GROUP_STATUS_ING);
    }

    private BooleanExpression siggAreaEq(Integer siggAreaid) {
        return siggAreaid == null ? null : siggArea.id.eq(siggAreaid);
    }

    private BooleanExpression sportEq(Integer sportId) {
       return sportId == null ? null : sport.id.eq(sportId);
    }

    private List<OrderSpecifier> sort(Pageable pageable) {
        List<OrderSpecifier> orders = new ArrayList<>();
        StringPath aliasLikes = Expressions.stringPath("likes");
        if (!pageable.getSort().isEmpty()) {
            for (Sort.Order order : pageable.getSort()) {
                Order direction =
                        order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                switch (order.getProperty()) {
                    case "likes" :
                        orders.add(new OrderSpecifier<>(direction, aliasLikes));
                        orders.add(new OrderSpecifier<>(Order.DESC, group.createdAt));
                        break;
                    case "date" :
                        orders.add(new OrderSpecifier<>(direction, group.createdAt));
                        break;
                    default:
                        break;
                }
            }
        } else {
            orders.add(new OrderSpecifier<>(Order.DESC, group.createdAt));
        }

        return orders;
    }

}
