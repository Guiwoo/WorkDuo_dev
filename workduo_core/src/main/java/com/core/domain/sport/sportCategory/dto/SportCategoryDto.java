package com.core.domain.sport.sportCategory.dto;

import com.core.domain.sport.sportCategory.SportCategory;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SportCategoryDto {

    private Integer id;
    private String name;

    public static SportCategoryDto fromEntity(SportCategory sportCategory) {
        return SportCategoryDto.builder()
                .id(sportCategory.getId())
                .name(sportCategory.getName())
                .build();
    }
}
