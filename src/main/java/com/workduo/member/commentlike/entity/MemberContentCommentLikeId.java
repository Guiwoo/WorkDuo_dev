package com.workduo.member.commentlike.entity;

import com.workduo.member.comment.entity.MemberContentComment;
import com.workduo.member.member.entity.Member;
import lombok.*;

import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class MemberContentCommentLikeId implements Serializable {
    private Member member;
    private MemberContentComment memberContentComment;
}
