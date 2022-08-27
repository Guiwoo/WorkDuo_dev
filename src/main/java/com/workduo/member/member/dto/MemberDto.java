package com.workduo.member.member.dto;

import com.workduo.member.member.entity.Member;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {
    private String email;
    private String username;
    private LocalDateTime createdAt;

    public static MemberDto fromEntity(Member member) {
        return MemberDto.builder()
                .email(member.getEmail())
                .username(member.getUsername())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
