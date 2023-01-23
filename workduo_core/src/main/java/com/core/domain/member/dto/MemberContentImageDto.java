package com.core.domain.member.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberContentImageDto {
    private Long id;
    private Long memberContentId;
    private String imagePath;

    @QueryProjection
    public MemberContentImageDto(Long id,Long memberContentId, String imagePath) {
        this.id = id;
        this.memberContentId = memberContentId;
        this.imagePath = imagePath;
    }
}
