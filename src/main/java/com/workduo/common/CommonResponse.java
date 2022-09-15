package com.workduo.common;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonResponse {

    private String success;
    private Map<String, Object> result;

    public static CommonResponse from() {
        return CommonResponse.builder()
                .success("T")
                .result(null)
                .build();
    }
}
