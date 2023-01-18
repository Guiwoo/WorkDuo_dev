package com.core.domain.area.siggArea.dto;

import com.core.domain.area.siggArea.SiggArea;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiggAreaDto {

    private String sgg;
    private String sidonm;
    private String sggnm;


    public static SiggAreaDto fromEntity(SiggArea siggArea) {
        return SiggAreaDto.builder()
                .sgg(siggArea.getSgg())
                .sidonm(siggArea.getSidonm())
                .sggnm(siggArea.getSggnm())
                .build();
    }
}
