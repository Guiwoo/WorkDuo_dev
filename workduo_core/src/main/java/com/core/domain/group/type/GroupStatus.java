package com.core.domain.group.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GroupStatus {
    GROUP_STATUS_ING("ING", "활동중인 그룹"),
    GROUP_STATUS_STOP("STOP", "정지된 그룹"),
    GROUP_STATUS_CANCEL("DELETE", "해지한 그룹");

    private final String value;
    private final String description;
}
