package com.workduo.area.sidoarea.dto;

import com.workduo.area.sidoarea.entity.SidoArea;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SidoAreaDto {

    private Integer id;
    private String admcd;
    private String sidonm;

    public static SidoAreaDto fromEntity(SidoArea sidoArea) {
        return SidoAreaDto.builder()
                .id(sidoArea.getId())
                .admcd(sidoArea.getAdmCd())
                .sidonm(sidoArea.getSidonm())
                .build();
    }
}
