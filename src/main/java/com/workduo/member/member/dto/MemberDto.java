package com.workduo.member.member.dto;

import com.workduo.member.member.entity.Member;
import com.workduo.member.member.type.MemberStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {
    private Long id;
    private String email;
    private String username;
    private String phoneNumber;
    private String password;
    private String nickname;
    private String status;
    private String profileImg;
    private MemberStatus memberStatus;
    private LocalDateTime createdAt;

    public static MemberDto fromEntity(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .username(member.getUsername())
                .memberStatus(member.getMemberStatus())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
