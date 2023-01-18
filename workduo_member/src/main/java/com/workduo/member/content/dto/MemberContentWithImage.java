package com.workduo.member.content.dto;

import com.core.domain.memberContent.entity.MemberContent;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberContentWithImage {
    private Long memberContentId;
    private String title;
    private LocalDateTime createdAt;
    private List<MemberContentImageDto> memberContentImageDto;

    public static MemberContentWithImage from(MemberContent mc, List<MemberContentImageDto> mcil){
        return MemberContentWithImage.builder()
                .memberContentId(mc.getId())
                .title(mc.getTitle())
                .createdAt(mc.getCreatedAt())
                .memberContentImageDto(mcil)
                .build();
    }
}
