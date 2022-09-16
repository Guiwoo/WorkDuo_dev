package com.workduo.group.gropcontent.dto.detailgroupcontent;

import com.querydsl.core.annotations.QueryProjection;
import com.workduo.group.gropcontent.entity.GroupContentImage;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class GroupContentImageDto {

    private Long id;
    private String imagePath;

    @QueryProjection
    public GroupContentImageDto(Long id, String imagePath) {
        this.id = id;
        this.imagePath = imagePath;
    }
}
