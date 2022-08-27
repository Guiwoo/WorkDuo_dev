package com.workduo.group.groupcreatemember.entity;

import com.workduo.group.group.entity.Group;
import com.workduo.member.member.entity.Member;
import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@IdClass(GroupCreateMemberId.class)
@Entity(name = "group_create_member")
@Table(name = "group_create_member")
public class GroupCreateMember {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;
}
