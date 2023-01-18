package com.workduo.member.member.dto.auth;

import com.core.domain.member.entity.MemberRole;
import com.workduo.member.member.dto.MemberRoleDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class MemberAuthDto {
    private Long id;

    private String username;
    private String password;
    private List<MemberRoleDto> roles = new ArrayList<>();

    @Builder
    public MemberAuthDto(Long id, String username, String password, List<MemberRole> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roles = roles.stream()
                .map(role -> MemberRoleDto.builder()
                        .role(role.getMemberRole())
                        .build())
                .collect(Collectors.toList());
    }
}