package com.workduo.group.gropcontent.repository.query.impl;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.workduo.group.gropcontent.dto.detailgroupcontent.*;
import com.workduo.group.gropcontent.repository.query.GroupContentQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.querydsl.core.types.Order.ASC;
import static com.querydsl.core.types.Order.DESC;
import static com.querydsl.jpa.JPAExpressions.select;
import static com.workduo.group.gropcontent.entity.QGroupContent.groupContent;
import static com.workduo.group.gropcontent.entity.QGroupContentComment.groupContentComment;
import static com.workduo.group.gropcontent.entity.QGroupContentCommentLike.groupContentCommentLike;
import static com.workduo.group.gropcontent.entity.QGroupContentImage.groupContentImage;
import static com.workduo.group.gropcontent.entity.QGroupContentLike.groupContentLike;
import static com.workduo.member.member.entity.QMember.member;

@Service
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
                                groupContent.id,
                                groupContent.title,
                                groupContent.content,
                                member.id.as("memberId"),
                                member.username,
                                member.nickname,
                                member.profileImg,
                                groupContent.deletedYn,
                                groupContent.createdAt,
                                Expressions.as(
                                        select(groupContentLike.count())
                                                .from(groupContentLike)
                                                .where(
                                                        groupContentLike
                                                                .groupContent
                                                                .eq(groupContent)
                                                )
                                        , "contentLike"
                                )
                        )
                )
                .distinct()
                .from(groupContent)
                .join(groupContent.member, member)
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
                .select(groupContent.count())
                .from(groupContent)
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
    public Optional<GroupContentDto> findByGroupContent(Long groupContentId) {
        return Optional.ofNullable(jpaQueryFactory
                .select(
                        new QGroupContentDto(
                                groupContent.id,
                                groupContent.title,
                                groupContent.content,
                                member.id.as("memberId"),
                                member.username,
                                member.nickname,
                                member.profileImg,
                                groupContent.deletedYn,
                                groupContent.createdAt,
                                Expressions.as(
                                        select(groupContentLike.count())
                                                .from(groupContentLike)
                                                .where(
                                                        groupContentLike
                                                                .groupContent
                                                                .eq(groupContent)
                                                )
                                        , "contentLike"
                                )
                        )
                )
                .distinct()
                .from(groupContent)
                .join(groupContent.member, member)
                .where(groupContent.id.eq(groupContentId))
                .fetchOne()
        );
    }

    @Override
    public List<GroupContentImageDto> findByGroupContentImage(Long groupContentId) {
        return jpaQueryFactory
                .select(
                        new QGroupContentImageDto(
                                groupContentImage.id,
                                groupContentImage.imagePath
                        )
                )
                .from(groupContentImage)
                .where(groupContentImage.groupContent.id.eq(groupContentId))
                .fetch();
    }

    @Override
    public Page<GroupContentCommentDto> findByGroupContentComments(
            Pageable pageable,
            Long groupContentId) {
        List<GroupContentCommentDto> content = jpaQueryFactory
                .select(
                        new QGroupContentCommentDto(
                                groupContentComment.id.as("commentId"),
                                member.id.as("memberId"),
                                member.username.as("username"),
                                member.nickname.as("nickname"),
                                member.profileImg.as("profileImg"),
                                groupContent.id.as("groupContentId"),
                                groupContentComment.comment.as("content"),
                                groupContentComment.createdAt.as("createdAt"),
                                Expressions.as(
                                        select(groupContentCommentLike.count())
                                                .from(groupContentCommentLike)
                                                .where(
                                                        groupContentCommentLike
                                                                .groupContentComment
                                                                .eq(groupContentComment)

                                                )
                                        , "commentLike"
                                )

                        )
                ).from(groupContentComment)
                .join(groupContentComment.member, member)
                .join(groupContentComment.groupContent, groupContent)
                .where(
                        commentUsDelYn(),
                        groupContentEq(groupContentId)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(findByGroupContentCommentsSort().stream()
                        .toArray(OrderSpecifier[]::new))
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(groupContentComment.count())
                .from(groupContentComment)
                .where(
                        commentUsDelYn(),
                        groupContentEq(groupContentId)
                );

        return PageableExecutionUtils.getPage(
                content,
                pageable,
                countQuery::fetchOne
        );
    }

    private BooleanExpression contentIsDelYn() {
        return groupContent.deletedYn.eq(false);
    }

    private BooleanExpression groupEq(Long groupId) {
        return groupContent.group.id.eq(groupId);
    }

    private BooleanExpression commentUsDelYn() {
        return groupContentComment.deletedYn.eq(false);
    }

    private BooleanExpression groupContentEq(Long groupContentId) {
        return groupContentId == null ? null : groupContent.id.eq(groupContentId);
    }

    private List<OrderSpecifier> findByGroupContentCommentsSort() {
        StringPath aliasCommentLikes = Expressions.stringPath("commentLike");

        List<OrderSpecifier> orders = new ArrayList<>(List.of(
                new OrderSpecifier<>(DESC, aliasCommentLikes),
                new OrderSpecifier<>(DESC, groupContentComment.createdAt)
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
                        orders.add(new OrderSpecifier(DESC, groupContent.createdAt));
                        break;
                    case "date" :
                        orders.add(new OrderSpecifier(direction, groupContent.createdAt));
                        break;
                    default:
                        break;
                }
            }
        } else {
            orders.add(new OrderSpecifier(DESC, groupContent.createdAt));
        }

        return orders;
    }
}
