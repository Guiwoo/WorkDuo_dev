package com.core.domain.memberCalendar.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MeetingActiveStatus {
    MEETING_ACTIVE_STATUS_ING("ING", "모임 참여 중"),
    MEETING_ACTIVE_STATUS_CANCEL("CANCEL", "모임 참여 취소"),
    MEETING_ACTIVE_STATUS_GROUP_LEADER_WITHDRAW("LEADER_GROUP_WITHDRAW", "그룹이 해지되어 모임 취소"),
    MEETING_ACTIVE_STATUS_DISMISS("DISMISS", "모임 해산");

    private final String value;
    private final String description;
}
