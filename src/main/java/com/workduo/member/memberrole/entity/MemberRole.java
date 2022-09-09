package com.workduo.member.memberrole.entity;

import com.workduo.member.member.entity.Member;
import com.workduo.member.memberrole.type.MemberRoleType;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.workduo.member.memberrole.type.MemberRoleType.ROLE_ADMIN;
import static com.workduo.member.memberrole.type.MemberRoleType.ROLE_MEMBER;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity(name = "member_role")
public class MemberRole {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_role_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private MemberRoleType memberRole; // 권한

}
