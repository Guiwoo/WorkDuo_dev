package com.workduo.group.group.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GroupJoinMemberStatus {
    GROUP_JOIN_MEMBER_STATUS_ING("ING", "그룹에서 활동중인 회원"),
    GROUP_JOIN_MEMBER_STATUS_STOP("STOP", "그룹에서 정지된 회원"),
    GROUP_JOIN_MEMBER_STATUS_WITHDRAW("WITHDRAW", "그룹에서 탈퇴한 회원"),
    GROUP_JOIN_MEMBER_STATUS_LEADER_WITHDRAW("LEADER_WITHDRAW", "그룹 리더가 그룹을 해지");

    private final String value;
    private final String description;
}
