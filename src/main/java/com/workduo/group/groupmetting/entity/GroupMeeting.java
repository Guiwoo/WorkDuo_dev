package com.workduo.group.groupmetting.entity;

import com.workduo.configuration.jpa.entitiy.BaseEntity;
import com.workduo.group.gropcontent.entity.GroupContent;
import com.workduo.member.member.entity.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity(name = "group_meeting")
@Table(name = "group_meeting")
public class GroupMeeting extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_meeting_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_content_id")
    private GroupContent groupContent;

    private Integer maxParticipant;
    private LocalDateTime meetingDate;

    @Lob
    private String location;

    private boolean deletedYn; // 삭제(탈퇴) 여부
    private LocalDateTime deletedAt; // 삭제(탈퇴) 날짜
}
