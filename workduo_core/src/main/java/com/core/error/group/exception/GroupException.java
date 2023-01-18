package com.core.error.group.exception;

import com.core.error.group.type.GroupErrorCode;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupException extends RuntimeException {
    private GroupErrorCode errorCode;
    private String errorMessage;

    public GroupException(GroupErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getMessage();
    }

}
