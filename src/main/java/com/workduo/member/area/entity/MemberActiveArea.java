package com.workduo.member.area.entity;

import com.workduo.area.siggarea.entity.SiggArea;
import com.workduo.member.member.entity.Member;
import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "member_active_area")
public class MemberActiveArea {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_active_area_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sgg")
    private SiggArea siggArea;
}
