package com.workduo.member.member.entity;

import com.workduo.member.member.entity.Member;
import com.workduo.sport.sport.entity.Sport;
import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "member_interested_sport")
public class MemberInterestedSport {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_interested_sport_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_id")
    private Sport sport;
}
