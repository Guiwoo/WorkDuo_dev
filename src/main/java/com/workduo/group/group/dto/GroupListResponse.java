package com.workduo.group.group.dto;

import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupListResponse {

    private String success;
    private GroupListDto result;

    public static GroupListResponse from(GroupListDto groupListDto) {
        return GroupListResponse.builder()
                .success("T")
                .result(groupListDto)
                .build();
    }

}
