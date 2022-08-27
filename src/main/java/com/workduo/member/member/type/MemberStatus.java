package com.workduo.member.member.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberStatus {
    MEMBER_STATUS_ING("ING","활동중인 회원"),
    MEMBER_STATUS_STOP("STOP","정지된 회원"),
    MEMBER_STATUS_WITHDRAW("WITHDRAW","탈퇴한 회원");

    private final String value;
    private final String description;
}
