package com.workduo.group.gropcontent.dto.detailgroupcontent;

import com.workduo.group.gropcontent.entity.GroupContentImage;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupContentImageDto {

    private Long id;
    private String imagePath;

    public static GroupContentImageDto fromEntity(GroupContentImage groupContentImage) {
        return GroupContentImageDto.builder()
                .id(groupContentImage.getId())
                .imagePath(groupContentImage.getImagePath())
                .build();
    }
}
