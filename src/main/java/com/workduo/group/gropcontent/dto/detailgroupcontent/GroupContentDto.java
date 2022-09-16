package com.workduo.group.gropcontent.dto.detailgroupcontent;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class GroupContentDto {

    private Long id;
    private String title;
    private String content;
    private Long memberId;
    private String username;
    private String nickname;
    private String profileImg;
    private boolean deletedYn;
    private LocalDateTime createdAt;
    private Long contentLike;

    @QueryProjection
    public GroupContentDto(Long id, String title, String content, Long memberId, String username, String nickname, String profileImg, boolean deletedYn, LocalDateTime createdAt, Long contentLike) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.memberId = memberId;
        this.username = username;
        this.nickname = nickname;
        this.profileImg = profileImg;
        this.deletedYn = deletedYn;
        this.createdAt = createdAt;
        this.contentLike = contentLike;
    }
}
