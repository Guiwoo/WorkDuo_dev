package com.workduo.group.groupmetting.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class TimeDto {
    private String date;
    private List<Time> times;

    public TimeDto() {
        times = new ArrayList<>();
    }

    public TimeDto(String date) {
        this.date = date;
        times = new ArrayList<>();
    }
}
