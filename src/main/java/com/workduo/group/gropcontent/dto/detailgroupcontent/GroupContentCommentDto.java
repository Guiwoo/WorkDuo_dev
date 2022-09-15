package com.workduo.group.gropcontent.dto.detailgroupcontent;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class GroupContentCommentDto {

    private Long commentId;
    private Long memberId;
    private String username;
    private String nickname;
    private String profileImg;
    private Long groupContentId;
    private String content;
    private LocalDateTime createdAt;
    private Long commentLike;

    @QueryProjection
    public GroupContentCommentDto(Long commentId, Long memberId, String username, String nickname, String profileImg, Long groupContentId, String content, LocalDateTime createdAt, Long commentLike) {
        this.commentId = commentId;
        this.memberId = memberId;
        this.username = username;
        this.nickname = nickname;
        this.profileImg = profileImg;
        this.groupContentId = groupContentId;
        this.content = content;
        this.createdAt = createdAt;
        this.commentLike = commentLike;
    }
}
