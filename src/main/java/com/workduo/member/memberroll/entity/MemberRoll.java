package com.workduo.member.memberroll.entity;

import com.workduo.member.member.entity.Member;
import com.workduo.member.member.type.MemberRole;
import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity(name = "member_roll")
public class MemberRoll {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_roll_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole; // 권한
}
