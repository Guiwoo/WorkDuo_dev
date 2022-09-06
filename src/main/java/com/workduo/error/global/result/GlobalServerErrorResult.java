package com.workduo.error.global.result;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GlobalServerErrorResult {
    private String success;
    private String message;
}
