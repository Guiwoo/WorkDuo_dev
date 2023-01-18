package com.core.domain.group.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class GroupCreateMemberId implements Serializable {
    private Long member;
    private Long group;
}
