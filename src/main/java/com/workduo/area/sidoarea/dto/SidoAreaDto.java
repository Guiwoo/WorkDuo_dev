package com.workduo.area.sidoarea.dto;

import com.workduo.area.sidoarea.entity.SidoArea;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SidoAreaDto {

    private String sido;
    private String sidonm;

    public static SidoAreaDto fromEntity(SidoArea sidoArea) {
        return SidoAreaDto.builder()
                .sido(sidoArea.getSido())
                .sidonm(sidoArea.getSidonm())
                .build();
    }
}
