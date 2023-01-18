package com.workduo.group.groupmetting.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class MeetingDto {

    private Long groupMeetingId;
    private Long groupId;
    private String title;
    private String content;
    private String location;
    private Integer maxParticipant;
    private Long participantCnt;
    private String createdAt;
    private String meetingDate;

    @QueryProjection
    public MeetingDto(Long groupMeetingId, Long groupId, String title, String content, String location, Integer maxParticipant, Long participantCnt, String createdAt, String meetingDate) {
        this.groupMeetingId = groupMeetingId;
        this.groupId = groupId;
        this.title = title;
        this.content = content;
        this.location = location;
        this.maxParticipant = maxParticipant;
        this.participantCnt = participantCnt;
        this.createdAt = createdAt;
        this.meetingDate = meetingDate;
    }
}
