package com.group.groupmetting.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Time {

    private String time;
    private boolean disabled;
}
