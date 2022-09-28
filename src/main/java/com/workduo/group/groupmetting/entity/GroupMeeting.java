package com.workduo.group.groupmetting.entity;

import com.workduo.configuration.jpa.entitiy.BaseEntity;
import com.workduo.error.group.exception.GroupException;
import com.workduo.error.group.type.GroupErrorCode;
import com.workduo.group.gropcontent.entity.GroupContent;
import com.workduo.group.group.entity.Group;
import com.workduo.group.groupmetting.dto.UpdateMeeting;
import com.workduo.member.member.entity.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

import static com.workduo.error.group.type.GroupErrorCode.GROUP_ALREADY_DELETE_GROUP;
import static com.workduo.error.group.type.GroupErrorCode.GROUP_MEETING_LESS_THEN_PARTICIPANT;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "group_meeting")
public class GroupMeeting extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_meeting_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    private String title;
    private String content;
    private Integer maxParticipant;
    private LocalDateTime meetingStartDate;
    private LocalDateTime meetingEndDate;

    @Lob
    private String location;

    private boolean deletedYn; // 삭제(탈퇴) 여부
    private LocalDateTime deletedAt; // 삭제(탈퇴) 날짜

    public void updateGroupMeeting(
            String title, String content, String location, int newMaxParticipant, int participants) throws GroupException {
        if (participants > newMaxParticipant) {
            throw new GroupException(GROUP_MEETING_LESS_THEN_PARTICIPANT);
        }

        this.title = title;
        this.content = content;
        this.location = location;
        this.maxParticipant = newMaxParticipant;
    }

    public void deleteGroupMeeting() {
        this.deletedYn = true;
        this.deletedAt = LocalDateTime.now();
    }
}
