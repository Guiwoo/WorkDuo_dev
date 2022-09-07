package com.workduo.group.group.repository.query.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.workduo.area.siggarea.dto.siggarea.SiggAreaDto;
import com.workduo.group.group.dto.GroupDto;
import com.workduo.group.group.dto.QGroupDto;
import com.workduo.group.group.repository.query.GroupQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.querydsl.core.types.Projections.bean;
import static com.workduo.area.sidoarea.entity.QSidoArea.sidoArea;
import static com.workduo.area.siggarea.entity.QSiggArea.siggArea;
import static com.workduo.group.group.entity.QGroup.group;
import static com.workduo.group.group.entity.QGroupJoinMember.groupJoinMember;
import static com.workduo.sport.sport.entity.QSport.sport;
import static com.workduo.sport.sportcategory.entity.QSportCategory.sportCategory;


@Repository
@RequiredArgsConstructor
public class GroupQueryRepositoryImpl implements GroupQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<GroupDto> findById(Long groupId) {
        return Optional.ofNullable(
                jpaQueryFactory
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
                                        groupJoinMember.group.count().as("participants")
                                )
                        ).from(group)
                        .join(group.siggArea, siggArea)
                        .join(siggArea.sidoArea, sidoArea)
                        .join(group.sport, sport)
                        .join(sport.sportCategory, sportCategory)
                        .leftJoin(groupJoinMember).on(group.id.eq(groupJoinMember.group.id))
                        .where(group.id.eq(groupId))
                        .groupBy(group.id)
                        .fetchOne()
        );
    }
}
