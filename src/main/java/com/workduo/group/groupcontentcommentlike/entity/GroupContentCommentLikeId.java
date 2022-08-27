package com.workduo.group.groupcontentcommentlike.entity;

import com.workduo.group.groupcontentcomment.entity.GroupContentComment;
import com.workduo.member.member.entity.Member;
import lombok.*;

import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class GroupContentCommentLikeId implements Serializable {
    private Member member;
    private GroupContentComment groupContentComment;
}
