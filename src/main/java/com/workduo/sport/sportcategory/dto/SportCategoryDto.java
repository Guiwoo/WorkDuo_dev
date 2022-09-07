package com.workduo.sport.sportcategory.dto;

import com.workduo.sport.sportcategory.entity.SportCategory;
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
