package com.core.domain.group.entity;

import com.core.domain.member.entity.Member;
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
