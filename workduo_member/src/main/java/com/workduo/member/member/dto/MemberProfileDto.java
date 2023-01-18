package com.workduo.member.member.dto;

import com.core.domain.member.entity.Member;
import com.core.domain.member.type.MemberStatus;
import com.workduo.member.content.dto.MemberContentWithImage;
import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberProfileDto {
    private Long id;
    private String username;
    private String nickname;
    private String profileImg;
    private String status;
    private MemberStatus memberStatus;
    private LocalDateTime createdAt;
    private Page<MemberContentWithImage> memberContentWithImageList;

    public static MemberProfileDto from(Member member, Page<MemberContentWithImage> list){
        return MemberProfileDto.builder()
                .id(member.getId())
                .username(member.getUsername())
                .nickname(member.getNickname())
                .status(member.getStatus())
                .profileImg(member.getProfileImg())
                .memberStatus(member.getMemberStatus())
                .createdAt(member.getCreatedAt())
                .memberContentWithImageList(list)
                .build();
    }
}
