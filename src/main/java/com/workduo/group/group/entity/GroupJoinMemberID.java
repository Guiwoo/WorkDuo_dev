package com.workduo.group.group.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class GroupJoinMemberID implements Serializable {
    private Long member;
    private Long group;
}
