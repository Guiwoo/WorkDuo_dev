package com.workduo.error.group.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GroupErrorCode {
    GROUP_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당하는 그룹이 없습니다."),
    GROUP_CREATE_MAXIMUM_EXCEEDED(HttpStatus.BAD_REQUEST, "그룹생성 최대개수를 초과하였습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
