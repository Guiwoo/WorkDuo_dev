package com.workduo.group.groupcontentcommentlike.entity;

import com.workduo.group.groupcontentcomment.entity.GroupContentComment;
import com.workduo.member.member.entity.Member;
import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "group_content_comment_like")
public class GroupContentCommentLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_content_comment_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_content_comment_id")
    private GroupContentComment groupContentComment;
}
