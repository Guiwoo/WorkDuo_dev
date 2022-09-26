package com.workduo.member.content.entity;

import com.workduo.member.member.entity.Member;
import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "member_content_comment_like")
public class MemberContentCommentLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_content_comment_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_content_comment_id")
    private MemberContentComment memberContentComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}