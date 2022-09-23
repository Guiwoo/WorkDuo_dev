package com.workduo.member.content.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MemberContentDto {
    private Long id;
    private String title;
    private String content;
    private boolean noticeYn;
    private int sortValue;
    private Long memberId;
    private String username;
    private String nickname;
    private String profileImg;
    private boolean deletedYn;
    private LocalDateTime createdAt;
    private Long count;

    @QueryProjection
    public MemberContentDto(Long id, String title, String content, boolean noticeYn, int sortValue, Long memberId, String username,String nickname, String profileImg, boolean deletedYn, LocalDateTime createdAt, Long count) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.noticeYn = noticeYn;
        this.sortValue = sortValue;
        this.memberId = memberId;
        this.username = username;
        this.nickname = nickname;
        this.profileImg = profileImg;
        this.deletedYn = deletedYn;
        this.createdAt = createdAt;
        this.count = count;
    }
}
