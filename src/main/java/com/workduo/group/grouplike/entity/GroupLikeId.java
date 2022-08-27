package com.workduo.group.grouplike.entity;

import com.workduo.group.group.entity.Group;
import com.workduo.member.member.entity.Member;
import lombok.*;

import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class GroupLikeId implements Serializable {
    private Member member;
    private Group group;
}
