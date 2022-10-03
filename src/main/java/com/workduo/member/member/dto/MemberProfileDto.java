package com.workduo.member.member.dto;

import com.workduo.member.content.dto.MemberContentDto;
import com.workduo.member.content.dto.MemberContentListDto;
import com.workduo.member.content.dto.MemberContentWithImage;
import com.workduo.member.member.entity.Member;
import com.workduo.member.member.type.MemberStatus;
import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

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
