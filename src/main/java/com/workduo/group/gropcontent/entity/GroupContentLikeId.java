package com.workduo.group.gropcontent.entity;

import com.workduo.group.gropcontent.entity.GroupContent;
import com.workduo.member.member.entity.Member;
import lombok.*;

import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class GroupContentLikeId implements Serializable {
    private Member member;
    private GroupContent groupContent;
}
