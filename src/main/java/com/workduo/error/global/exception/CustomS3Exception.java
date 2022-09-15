package com.workduo.error.global.exception;

import com.workduo.error.global.type.GlobalExceptionType;
import com.workduo.error.group.type.GroupErrorCode;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomS3Exception extends RuntimeException{

    private GlobalExceptionType errorCode;
    private String errorMessage;

    public CustomS3Exception(GlobalExceptionType errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getMessage();
    }
}
