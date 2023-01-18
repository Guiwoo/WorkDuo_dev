package com.core.error.global.result;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GlobalErrorResult {
    private String success;
    private String message;
    private String result;
}
