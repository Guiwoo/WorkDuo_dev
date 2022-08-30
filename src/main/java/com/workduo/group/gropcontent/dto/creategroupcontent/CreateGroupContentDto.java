package com.workduo.group.gropcontent.dto.creategroupcontent;

import com.workduo.group.gropcontent.entity.GroupContent;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateGroupContentDto {

    private Long id;
    private Long memberId;
    private Long groupId;
    private String title;
    private String content;
    private boolean activate;
    private String thumbnailPath;
    private LocalDateTime createdAt;

    public static CreateGroupContentDto fromEntity(GroupContent groupContent) {
        return CreateGroupContentDto.builder()
                .id(groupContent.getId())
                .memberId(groupContent.getMember().getId())
                .groupId(groupContent.getGroup().getId())
                .title(groupContent.getTitle())
                .content(groupContent.getContent())
                .activate(groupContent.isActivate())
                .thumbnailPath(groupContent.getThumbnailPath())
                .createdAt(groupContent.getCreatedAt())
                .build();
    }
}
