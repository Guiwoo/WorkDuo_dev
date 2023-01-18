package com.core.error.group.result;

import com.core.error.group.type.GroupErrorCode;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupErrorResult {
    private String success;
    private String errorCode;
    private String errorMessage;

    public static GroupErrorResult of(GroupErrorCode groupErrorCode) {
        return GroupErrorResult.builder()
                .success("F")
                .errorCode(groupErrorCode.name())
                .errorMessage(groupErrorCode.getMessage())
                .build();
    }
}
