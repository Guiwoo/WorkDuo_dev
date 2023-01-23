package com.group.gropcontent.repository.query.impl;

import com.core.domain.groupContent.dto.*;
import com.core.domain.groupContent.entity.*;
import com.core.domain.member.entity.QMember;
import com.group.gropcontent.repository.query.GroupContentQueryRepository;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
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


@Repository
@RequiredArgsConstructor
public class GroupContentQueryRepositoryImpl implements GroupContentQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<GroupContentDto> findByGroupContentList(
            Pageable pageable,
            Long groupId) {
        List<GroupContentDto> content = jpaQueryFactory
                .select(
                        new QGroupContentDto(
                                QGroupContent.groupContent.id,
                                QGroupContent.groupContent.title,
                                QGroupContent.groupContent.content,
                                QMember.member.id.as("memberId"),
                                QMember.member.username,
                                QMember.member.nickname,
                                QMember.member.profileImg,
                                QGroupContent.groupContent.deletedYn,
                                QGroupContent.groupContent.createdAt,
                                Expressions.as(
                                        JPAExpressions.select(QGroupContentLike.groupContentLike.count())
                                                .from(QGroupContentLike.groupContentLike)
                                                .where(
                                                        QGroupContentLike.groupContentLike
                                                                .groupContent
                                                                .eq(QGroupContent.groupContent)
                                                )
                                        , "contentLike"
                                )
                        )
                )
                .distinct()
                .from(QGroupContent.groupContent)
                .join(QGroupContent.groupContent.member, QMember.member)
                .where(
                        contentIsDelYn(),
                        groupEq(groupId)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(findByGroupContentListSort(pageable).stream()
                        .toArray(OrderSpecifier[]::new))
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(QGroupContent.groupContent.count())
                .from(QGroupContent.groupContent)
                .where(
                        contentIsDelYn(),
                        groupEq(groupId)
                );


        return PageableExecutionUtils.getPage(
                content,
                pageable,
                countQuery::fetchOne
        );
    }

    @Override
    public Optional<GroupContentDto> findByGroupContent(Long groupId, Long groupContentId) {

        return Optional.ofNullable(jpaQueryFactory
                .select(
                        new QGroupContentDto(
                                QGroupContent.groupContent.id,
                                QGroupContent.groupContent.title,
                                QGroupContent.groupContent.content,
                                QMember.member.id.as("memberId"),
                                QMember.member.username,
                                QMember.member.nickname,
                                QMember.member.profileImg,
                                QGroupContent.groupContent.deletedYn,
                                QGroupContent.groupContent.createdAt,
                                Expressions.as(
                                        JPAExpressions.select(QGroupContentLike.groupContentLike.count())
                                                .from(QGroupContentLike.groupContentLike)
                                                .where(
                                                        QGroupContentLike.groupContentLike
                                                                .groupContent
                                                                .eq(QGroupContent.groupContent)
                                                )
                                        , "contentLike"
                                )
                        )
                )
                .distinct()
                .from(QGroupContent.groupContent)
                .join(QGroupContent.groupContent.member, QMember.member)
                .where(
                        groupContentEq(groupContentId),
                        groupEq(groupId)
                )
                .fetchOne()
        );
    }

    @Override
    public List<GroupContentImageDto> findByGroupContentImage(Long groupId, Long groupContentId) {
        return jpaQueryFactory
                .select(
                        new QGroupContentImageDto(
                                QGroupContentImage.groupContentImage.id,
                                QGroupContentImage.groupContentImage.imagePath
                        )
                )
                .from(QGroupContentImage.groupContentImage)
                .where(
                        QGroupContentImage.groupContentImage.groupContent.id.eq(groupContentId),
                        groupEq(groupId)
                )
                .fetch();
    }

    @Override
    public Page<GroupContentCommentDto> findByGroupContentComments(
            Pageable pageable,
            Long groupId,
            Long groupContentId) {
        List<GroupContentCommentDto> content = jpaQueryFactory
                .select(
                        new QGroupContentCommentDto(
                                QGroupContentComment.groupContentComment.id.as("commentId"),
                                QMember.member.id.as("memberId"),
                                QMember.member.username.as("username"),
                                QMember.member.nickname.as("nickname"),
                                QMember.member.profileImg.as("profileImg"),
                                QGroupContent.groupContent.id.as("groupContentId"),
                                QGroupContentComment.groupContentComment.comment.as("content"),
                                QGroupContentComment.groupContentComment.createdAt.as("createdAt"),
                                Expressions.as(
                                        JPAExpressions.select(QGroupContentCommentLike.groupContentCommentLike.count())
                                                .from(QGroupContentCommentLike.groupContentCommentLike)
                                                .where(
                                                        QGroupContentCommentLike.groupContentCommentLike
                                                                .groupContentComment
                                                                .eq(QGroupContentComment.groupContentComment)

                                                )
                                        , "commentLike"
                                )

                        )
                ).from(QGroupContentComment.groupContentComment)
                .join(QGroupContentComment.groupContentComment.member, QMember.member)
                .join(QGroupContentComment.groupContentComment.groupContent, QGroupContent.groupContent)
                .where(
                        commentIsDelYn(),
                        groupEq(groupId),
                        groupContentEq(groupContentId)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(findByGroupContentCommentsSort().stream()
                        .toArray(OrderSpecifier[]::new))
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(QGroupContentComment.groupContentComment.count())
                .from(QGroupContentComment.groupContentComment)
                .where(
                        commentIsDelYn(),
                        groupEq(groupId),
                        groupContentEq(groupContentId)
                );

        return PageableExecutionUtils.getPage(
                content,
                pageable,
                countQuery::fetchOne
        );
    }

    private BooleanExpression contentIsDelYn() {
        return QGroupContent.groupContent.deletedYn.eq(false);
    }

    private BooleanExpression groupEq(Long groupId) {
        return QGroupContent.groupContent.group.id.eq(groupId);
    }

    private BooleanExpression commentIsDelYn() {
        return QGroupContentComment.groupContentComment.deletedYn.eq(false);
    }

    private BooleanExpression groupContentEq(Long groupContentId) {
        return groupContentId == null ? null : QGroupContent.groupContent.id.eq(groupContentId);
    }

    private List<OrderSpecifier> findByGroupContentCommentsSort() {
        StringPath aliasCommentLikes = Expressions.stringPath("commentLike");

        List<OrderSpecifier> orders = new ArrayList<>(List.of(
                new OrderSpecifier<>(DESC, aliasCommentLikes),
                new OrderSpecifier<>(DESC, QGroupContentComment.groupContentComment.createdAt)
        ));

        return orders;
    }

    private List<OrderSpecifier> findByGroupContentListSort(Pageable pageable) {
        List<OrderSpecifier> orders = new ArrayList<>();
        StringPath aliasContentLike =
                Expressions.stringPath("contentLike");

        if (!pageable.getSort().isEmpty()) {
            for (Sort.Order order : pageable.getSort()) {
                Order direction =
                        order.getDirection().isAscending() ? ASC : DESC;

                switch (order.getProperty()) {
                    case "likes":
                        orders.add(new OrderSpecifier(direction, aliasContentLike));
                        orders.add(new OrderSpecifier(DESC, QGroupContent.groupContent.createdAt));
                        break;
                    case "date" :
                        orders.add(new OrderSpecifier(direction, QGroupContent.groupContent.createdAt));
                        break;
                    default:
                        break;
                }
            }
        } else {
            orders.add(new OrderSpecifier(DESC, QGroupContent.groupContent.createdAt));
        }

        return orders;
    }
}
