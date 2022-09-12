package com.workduo.member.member.dto;

import lombok.*;

import java.util.Map;

public class MemberWithdraw {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response{
        private String success;
        private Map<String,String> result;

        public static MemberCreate.Response from(){
            return MemberCreate.Response.builder()
                    .success("T")
                    .result(null)
                    .build();
        }
    }
}
