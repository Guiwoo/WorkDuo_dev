package com.workduo.area.siggarea.dto.siggarea;

import com.workduo.area.siggarea.entity.SiggArea;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiggAreaDto {

    private Integer id;
    private Integer cityId;
    private String amdCd;
    private String sggnm;


    public static SiggAreaDto fromEntity(SiggArea siggArea) {
        return SiggAreaDto.builder()
                .id(siggArea.getId())
                .cityId(siggArea.getCityId())
                .amdCd(siggArea.getAmdCd())
                .sggnm(siggArea.getSggnm())
                .build();
    }
}
