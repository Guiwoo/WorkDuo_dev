package com.core.domain.memberCalendar.entity;

import com.core.domain.group.entity.Group;
import com.core.domain.groupMeeting.entity.GroupMeeting;
import com.core.domain.member.entity.Member;
import com.core.domain.memberCalendar.type.MeetingActiveStatus;
import lombok.*;

import javax.persistence.*;

import static com.core.domain.memberCalendar.type.MeetingActiveStatus.MEETING_ACTIVE_STATUS_CANCEL;

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

    public void cancelMeeting() {
        this.meetingActiveStatus = MEETING_ACTIVE_STATUS_CANCEL;
    }
}
