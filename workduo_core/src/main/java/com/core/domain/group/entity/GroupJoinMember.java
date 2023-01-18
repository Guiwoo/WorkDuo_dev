package com.core.domain.group.entity;

import com.core.domain.base.BaseEntity;
import com.core.domain.group.type.GroupJoinMemberStatus;
import com.core.domain.group.type.GroupRole;
import com.core.domain.member.entity.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

import static com.core.domain.group.type.GroupJoinMemberStatus.GROUP_JOIN_MEMBER_STATUS_WITHDRAW;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "group_join_member")
public class GroupJoinMember extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_join_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Enumerated(EnumType.STRING)
    private GroupRole groupRole;

    @Enumerated(EnumType.STRING)
    private GroupJoinMemberStatus groupJoinMemberStatus;
    private LocalDateTime deletedAt; // 삭제(탈퇴) 날짜

    public void withdrawGroup() {
        this.groupJoinMemberStatus = GROUP_JOIN_MEMBER_STATUS_WITHDRAW;
        this.deletedAt = LocalDateTime.now();
    }
}
