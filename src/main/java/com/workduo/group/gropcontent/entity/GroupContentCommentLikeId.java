package com.workduo.group.gropcontent.entity;

import com.workduo.group.gropcontent.entity.GroupContentComment;
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
