package com.workduo.group.group.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.workduo.area.sidoarea.dto.SidoAreaDto;
import com.workduo.area.siggarea.dto.siggarea.SiggAreaDto;
import com.workduo.area.sidoarea.entity.SidoArea;
import com.workduo.area.siggarea.entity.SiggArea;
import com.workduo.group.group.entity.Group;
import com.workduo.sport.sport.dto.SportDto;
import com.workduo.sport.sport.entity.Sport;
import com.workduo.sport.sportcategory.dto.SportCategoryDto;
import com.workduo.sport.sportcategory.entity.SportCategory;
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

//    public static GroupDto fromEntity(Group group) {
//        return GroupDto.builder()
//                .groupId(group.getId())
//                .name(group.getName())
//                .limitPerson(group.getLimitPerson())
//                .siggArea(SiggAreaDto.fromEntity(group.getSiggArea()))
//                .sidoArea(SidoAreaDto.fromEntity(group.getSiggArea().getSidoArea()))
//                .sport(SportDto.fromEntity(group.getSport()))
//                .sportCategory(SportCategoryDto.fromEntity(group.getSport().getSportCategory()))
//                .introduce(group.getIntroduce())
//                .thumbnailPath(group.getThumbnailPath())
//                .build();
//    }
}
