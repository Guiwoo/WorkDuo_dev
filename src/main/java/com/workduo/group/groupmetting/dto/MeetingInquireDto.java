package com.workduo.group.groupmetting.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class MeetingInquireDto {

    private String time;
    private Integer term;

    @QueryProjection
    public MeetingInquireDto(String time, Integer term) {
        this.time = time;
        this.term = term;
    }
}
