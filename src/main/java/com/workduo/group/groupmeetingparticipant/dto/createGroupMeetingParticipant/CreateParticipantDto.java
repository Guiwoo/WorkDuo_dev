package com.workduo.group.groupmeetingparticipant.dto.createGroupMeetingParticipant;

import com.workduo.group.groupmeetingparticipant.entity.GroupMeetingParticipant;
import com.workduo.group.groupmetting.entity.GroupMeeting;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateParticipantDto {

    private Long groupMeetingId;
    private Long memberId;
    private LocalDateTime meetingDate;

    public static CreateParticipantDto fromEntity(
            GroupMeetingParticipant groupMeetingParticipant,
            GroupMeeting groupMeeting) {

        return CreateParticipantDto.builder()
                .groupMeetingId(groupMeetingParticipant.getGroupMeeting().getId())
                .memberId(groupMeetingParticipant.getMember().getId())
                .meetingDate(groupMeeting.getMeetingDate())
                .build();
    }
}
