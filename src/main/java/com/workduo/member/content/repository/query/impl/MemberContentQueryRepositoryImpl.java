package com.workduo.member.content.repository.query.impl;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.workduo.member.content.dto.*;
import com.workduo.member.content.repository.query.MemberContentQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.querydsl.core.types.Order.ASC;
import static com.querydsl.core.types.Order.DESC;
import static com.querydsl.jpa.JPAExpressions.select;
import static com.workduo.member.content.entity.QMemberContent.memberContent;
import static com.workduo.member.content.entity.QMemberContentComment.memberContentComment;
import static com.workduo.member.content.entity.QMemberContentCommentLike.memberContentCommentLike;
import static com.workduo.member.content.entity.QMemberContentLike.memberContentLike;
import static com.workduo.member.contentimage.entitiy.QMemberContentImage.memberContentImage;
import static com.workduo.member.member.entity.QMember.member;

@Repository
@RequiredArgsConstructor
public class MemberContentQueryRepositoryImpl implements MemberContentQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    /**
     * 전체 게시글 가져오는 페이지
     * @param pageable
     * @param realMember
     * @return
     */

    /**
     * 활동 지역 위주로 가져오는 페이지 고민을 해보자.(latitude,longitude)
     * where 지역 in()
     * 사이즈가 0 이라면 계속 전체 게시글로
     */

    @Override
    public Page<MemberContentListDto> findByContentList(Pageable pageable) {
        List<MemberContentDto> fetch = jpaQueryFactory.select(
                        new QMemberContentDto(
                                memberContent.id,
                                memberContent.title,
                                memberContent.content,
                                memberContent.noticeYn,
                                memberContent.sortValue,
                                member.id,
                                member.username,
                                member.nickname,
                                member.profileImg,
                                memberContent.deletedYn,
                                memberContent.createdAt,
                                select(memberContentLike.count())
                                        .from(memberContentLike)
                                        .where(memberContentLike.memberContent.eq(
                                                memberContent
                                        ))
                        )
                )
                .distinct()
                .from(memberContent)
                .join(memberContent.member, member)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(findByMemberContentListSort(pageable)
                        .toArray(OrderSpecifier[]::new))
                .fetch();

        // 리턴용 리스트
        List<MemberContentListDto> list = new ArrayList<>();
        // 컨탠트 아이디 닮을 리스트
        List<Long> contentIdList = new ArrayList<>();
        // 리스트에 넣어주면서, 이미지 담을 컬렉션 생성
        for (MemberContentDto memberContentDto : fetch) {
            contentIdList.add(memberContentDto.getId());
            list.add(MemberContentListDto.from(memberContentDto,new ArrayList<>()));
        }
        // 이미지 한방 쿼리 작성
        List<MemberContentImageDto> allImageByMemberContent
                = getAllImageByMemberContent(contentIdList);

        // 이중 포문 돌면서 맞는 아이디에 집어넣어주기
        for (MemberContentImageDto memberContentImageDto : allImageByMemberContent) {
            for(MemberContentListDto mcld : list){
                if(memberContentImageDto.getMemberContentId() == mcld.getId()){
                    mcld.getMemberContentImages().add(memberContentImageDto);
                    break;
                }
            }
        }

        //총 컨탠트 갯수 가져오기
        JPAQuery<Long> total = jpaQueryFactory
                .select(memberContent.count())
                .from(memberContent);

        //사진은 어떻게 가져올래 ? 따로 ?
        return PageableExecutionUtils.getPage(list, pageable, total::fetchOne);
    }

    /**
     * 멤버 디테일
     * @param memberContentId
     * @return
     */
    @Override
    public MemberContentDto getContentDetail(Long memberContentId) {
        MemberContentDto memberContentDto = jpaQueryFactory.select(
                        new QMemberContentDto(
                                memberContent.id,
                                memberContent.title,
                                memberContent.content,
                                memberContent.noticeYn,
                                memberContent.sortValue,
                                member.id,
                                member.username,
                                member.nickname,
                                member.profileImg,
                                memberContent.deletedYn,
                                memberContent.createdAt,
                                select(memberContentLike.count())
                                        .from(memberContentLike)
                                        .where(
                                                memberContentLike.memberContent.eq(
                                                memberContent)
                                        )
                        )
                )
                .from(memberContent)
                .join(memberContent.member, member)
                .where(
                        memberContent.id.eq(memberContentId),
                        memberContent.deletedYn.eq(false)
                )
                .fetchOne();

        return Objects.requireNonNull(memberContentDto);
    }

    /**
     * 코멘트 가져오기
     * @param memberContentId
     * @param pageable
     * @return
     */
    @Override
    public Page<MemberContentCommentDto> getCommentByContent(Long memberContentId, Pageable pageable) {
        List<MemberContentCommentDto> list = jpaQueryFactory
                .select(new QMemberContentCommentDto(
                                memberContentComment.id,
                                member.id,
                                member.username,
                                memberContentComment.content,
                                member.nickname,
                                member.profileImg,
                                Expressions.as(
                                        select(memberContentCommentLike.count())
                                                .from(memberContentCommentLike)
                                                .where(
                                                        memberContentCommentLike
                                                                .memberContentComment
                                                                .eq(memberContentComment)
                                                ), "likeCnt"),
                                memberContentComment.createdAt
                        )
                )
                .from(memberContentComment)
                .join(memberContentComment.member,member)
                .join(memberContentComment.memberContent,memberContent)
                .where(
                        memberContentComment.memberContent.id.eq(memberContentId),
                        memberContentComment.deletedYn.eq(false)
                )
                .groupBy(memberContentComment)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(findByContentComment().toArray(OrderSpecifier[]::new))
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(memberContentComment.count())
                .from(memberContentComment)
                .where(memberContentComment.memberContent.id.eq(memberContentId));

        return PageableExecutionUtils.getPage(
                list,
                pageable,
                countQuery::fetchOne);
    }

    @Override
    public List<MemberContentImageDto> getByMemberContent(Long memberContentId) {
        return jpaQueryFactory.select(
                new QMemberContentImageDto(
                        memberContentImage.id,
                        memberContentImage.memberContent.id,
                        memberContentImage.imgPath
                )
        ).from(memberContentImage)
                .where(memberContentImage.memberContent.id.eq(memberContentId))
                .fetch();
    }

    @Override
    public List<MemberContentImageDto> getAllImageByMemberContent(List<Long> memberContentIdList) {
        return jpaQueryFactory.select(
                        new QMemberContentImageDto(
                                memberContentImage.id,
                                memberContentImage.memberContent.id,
                                memberContentImage.imgPath
                        )
                ).from(memberContentImage)
                .where(memberContentImage.memberContent.id.in(memberContentIdList))
                .fetch();
    }

    private List<OrderSpecifier> findByMemberContentListSort(Pageable pageable){
        List<OrderSpecifier> orders = new ArrayList<>();

        StringPath aliasLike = Expressions.stringPath("count");

        if(!pageable.getSort().isEmpty()){
            for(Sort.Order order : pageable.getSort()){
                Order direction =
                        order.getDirection().isAscending() ? ASC :DESC;
                switch (order.getProperty()){
                    case "likes":
                        orders.add(new OrderSpecifier(direction,aliasLike));
                        orders.add(new OrderSpecifier(DESC, memberContent.createdAt));
                        break;
                    default:
                        orders.add(new OrderSpecifier(direction, memberContent.createdAt));
                        break;
                }
            }
        }else{
            orders.add(new OrderSpecifier(DESC, memberContent.createdAt));
        }
        return orders;
    }

    private List<OrderSpecifier> findByContentComment() {
        StringPath aliasCommentLikes = Expressions.stringPath("likeCnt");

        List<OrderSpecifier> orders = new ArrayList<>(List.of(
                new OrderSpecifier<>(DESC, aliasCommentLikes),
                new OrderSpecifier<>(DESC, memberContentComment.createdAt)
        ));

        return orders;
    }

}
