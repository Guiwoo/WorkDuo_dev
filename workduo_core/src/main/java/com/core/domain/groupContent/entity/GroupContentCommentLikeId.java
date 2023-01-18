package com.core.domain.groupContent.entity;

import com.core.domain.member.entity.Member;
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
