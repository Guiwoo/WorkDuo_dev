package com.workduo.member.content.entity;

import com.workduo.configuration.jpa.entitiy.BaseEntity;
import com.workduo.member.member.entity.Member;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "member_content")
public class MemberContent extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_content_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String title;

    @Lob
    private String content;

    private boolean noticeYn;

    private int sortValue;

    private boolean deletedYn; // 삭제(탈퇴) 여부
    private LocalDateTime deletedAt; // 삭제(탈퇴) 날짜
}
