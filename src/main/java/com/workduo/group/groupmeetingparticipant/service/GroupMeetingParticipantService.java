package com.workduo.group.groupmeetingparticipant.service;

import com.workduo.group.groupmeetingparticipant.dto.createGroupMeetingParticipant.CreateParticipant;
import com.workduo.group.groupmeetingparticipant.dto.createGroupMeetingParticipant.CreateParticipantDto;

public interface GroupMeetingParticipantService {
    CreateParticipantDto meetingParticipant(CreateParticipant.Request request, Long memberId) throws InterruptedException;
}
