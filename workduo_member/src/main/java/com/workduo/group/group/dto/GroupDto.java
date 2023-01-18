package com.workduo.group.group.dto;

import com.core.domain.area.sidoArea.dto.SidoAreaDto;
import com.core.domain.area.siggArea.dto.SiggAreaDto;
import com.core.domain.sport.sport.dto.SportDto;
import com.core.domain.sport.sportCategory.dto.SportCategoryDto;
import com.querydsl.core.annotations.QueryProjection;
import com.core.domain.area.sidoArea.SidoArea;
import com.core.domain.area.siggArea.SiggArea;
import com.core.domain.sport.sportCategory.SportCategory;
import com.core.domain.sport.sport.Sport;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupDto {

    private Long groupId;
    private String name;
    private Integer limitPerson;
    private SiggAreaDto siggArea;
    private SidoAreaDto sidoArea;
    private SportDto sport;
    private SportCategoryDto sportCategory;
    private String introduce;
    private String thumbnailPath;
    private Long participants;
    private Long likes;

    @QueryProjection
    public GroupDto(Long groupId, String name, Integer limitPerson, SiggArea siggArea, SidoArea sidoArea, Sport sport, SportCategory sportCategory, String introduce, String thumbnailPath, Long participants, Long likes) {
        this.groupId = groupId;
        this.name = name;
        this.limitPerson = limitPerson;
        this.siggArea = SiggAreaDto.fromEntity(siggArea);
        this.sidoArea = SidoAreaDto.fromEntity(sidoArea);
        this.sport = SportDto.fromEntity(sport);
        this.sportCategory = SportCategoryDto.fromEntity(sportCategory);
        this.introduce = introduce;
        this.thumbnailPath = thumbnailPath;
        this.participants = participants;
        this.likes = likes;
    }
}
