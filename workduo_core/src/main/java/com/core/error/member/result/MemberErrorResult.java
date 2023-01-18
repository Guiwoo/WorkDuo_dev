package com.core.error.member.result;

import com.core.error.member.type.MemberErrorCode;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberErrorResult {
    private String success;
    private String errorCode;
    private String errorMessage;

    public static MemberErrorResult of(MemberErrorCode memberErrorCode) {
        return MemberErrorResult.builder()
                .success("F")
                .errorCode(memberErrorCode.name())
                .errorMessage(memberErrorCode.getMessage())
                .build();
    }
}

