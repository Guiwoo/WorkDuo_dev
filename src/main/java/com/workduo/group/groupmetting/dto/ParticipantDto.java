package com.workduo.group.groupmetting.dto;

import com.workduo.group.groupmetting.entity.GroupMeetingParticipant;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantDto {

    private Long memberId;
    private String username;
    private String nickname;
    private String profileImg;

    public static ParticipantDto fromEntity(GroupMeetingParticipant groupMeetingParticipant) {
        return ParticipantDto.builder()
                .memberId(groupMeetingParticipant.getMember().getId())
                .username(groupMeetingParticipant.getMember().getUsername())
                .nickname(groupMeetingParticipant.getMember().getNickname())
                .profileImg(groupMeetingParticipant.getMember().getProfileImg())
                .build();
    }
}
