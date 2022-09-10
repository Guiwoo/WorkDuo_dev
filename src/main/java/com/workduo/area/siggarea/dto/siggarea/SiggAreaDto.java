package com.workduo.area.siggarea.dto.siggarea;

import com.workduo.area.siggarea.entity.SiggArea;
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
