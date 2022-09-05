package com.workduo.error.group.exception;

import com.workduo.error.group.type.GroupErrorCode;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupException extends RuntimeException{
    private GroupErrorCode errorCode;
    private String errorMessage;

    public GroupException(GroupErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getMessage();
    }

}
