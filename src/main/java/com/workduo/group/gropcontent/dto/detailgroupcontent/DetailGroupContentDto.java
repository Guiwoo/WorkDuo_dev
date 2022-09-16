package com.workduo.group.gropcontent.dto.detailgroupcontent;

import com.workduo.group.gropcontent.entity.GroupContent;
import com.workduo.group.gropcontent.entity.GroupContentImage;
import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private Long memberId;
    private String username;
    private String nickname;
    private String profileImg;
    private LocalDateTime createdAt;
    private Long contentLike;
    private List<GroupContentImageDto> groupContentImages = new ArrayList<>();
    private Page<GroupContentCommentDto> groupContentComments;

    public static DetailGroupContentDto from(
            GroupContentDto groupContent,
            List<GroupContentImageDto> groupContentImages,
            Page<GroupContentCommentDto> groupContentComments) {
    return DetailGroupContentDto.builder()
            .id(groupContent.getId())
            .title(groupContent.getTitle())
            .content(groupContent.getContent())
            .memberId(groupContent.getMemberId())
            .username(groupContent.getUsername())
            .nickname(groupContent.getNickname())
            .profileImg(groupContent.getProfileImg())
            .createdAt(groupContent.getCreatedAt())
            .contentLike(groupContent.getContentLike())
            .groupContentImages(groupContentImages)
            .groupContentComments(groupContentComments)
            .build();
    }
}
