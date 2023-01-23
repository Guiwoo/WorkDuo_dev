package com.group.group.dto;


import com.core.domain.group.entity.Group;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupThumbnail {

    private String thumbnailPath;

    public static GroupThumbnail fromEntity(Group group) {
        return GroupThumbnail.builder()
                .thumbnailPath(group.getThumbnailPath())
                .build();
    }
}
