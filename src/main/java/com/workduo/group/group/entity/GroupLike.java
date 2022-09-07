package com.workduo.group.group.entity;

import com.workduo.group.group.entity.Group;
import com.workduo.member.member.entity.Member;
import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "group_like")
public class GroupLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;
}
