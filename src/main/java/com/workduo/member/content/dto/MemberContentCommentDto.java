package com.workduo.member.content.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MemberContentCommentDto {
    private Long id;
    private Long memberId;
    private String username;
    private String content;
    private String nickname;
    private String profileImg;
    private Long likeCnt;
    private LocalDateTime createdAt;

    @QueryProjection
    public MemberContentCommentDto(Long id, Long memberId, String username, String content, String nickname, String profileImg, Long likeCnt, LocalDateTime createdAt) {
        this.id = id;
        this.memberId = memberId;
        this.username = username;
        this.content = content;
        this.nickname = nickname;
        this.profileImg = profileImg;
        this.likeCnt = likeCnt;
        this.createdAt = createdAt;
    }
}
