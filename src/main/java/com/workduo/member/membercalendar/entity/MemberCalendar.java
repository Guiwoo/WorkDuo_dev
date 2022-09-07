package com.workduo.member.membercalendar.entity;

import com.workduo.group.group.entity.Group;
import com.workduo.group.groupmetting.entity.GroupMeeting;
import com.workduo.member.member.entity.Member;
import com.workduo.member.membercalendar.type.MeetingActiveStatus;
import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "member_calendar")
public class MemberCalendar {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_calendar_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_meeting_id")
    private GroupMeeting groupMeeting;

    @Enumerated(EnumType.STRING)
    private MeetingActiveStatus meetingActiveStatus;
}
