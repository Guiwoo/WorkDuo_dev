package com.workduo.group.gropcontent.dto.detailgroupcontent;

import com.workduo.group.gropcontent.entity.GroupContent;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetailGroupContentDto {

    private Long id;
    private String title;
    private String content;
    private List<GroupContentImageDto> groupContentImages;

//    public static DetailGroupContentDto fromEntity(GroupContent groupContent) {
//        return DetailGroupContentDto.builder()
//                .id(groupContent.getId())
//                .title(groupContent.getTitle())
//                .content(groupContent.getContent())
//                .groupContentImages(
//                        groupContent.getGroupContentImages().stream()
//                                .map(groupContentImage ->
//                                        GroupContentImageDto.fromEntity(groupContentImage)
//                                )
//                                .collect(Collectors.toList())
//                )
//                .build();
//    }
}
