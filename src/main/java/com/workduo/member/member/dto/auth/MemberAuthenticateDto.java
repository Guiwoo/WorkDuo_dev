package com.workduo.member.member.dto.auth;

import com.workduo.member.memberrole.dto.MemberRoleDto;
import com.workduo.member.memberrole.entity.MemberRole;
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
public class MemberAuthenticateDto {
    private String email;
    private List<MemberRoleDto> roles = new ArrayList<>();



    @Builder
    public MemberAuthenticateDto(String email, List<MemberRole> roles) {
       this.email =email;
       this.roles = roles.stream()
               .map(role -> MemberRoleDto.builder()
                       .role(role.getMemberRole())
                       .build())
               .collect(Collectors.toList());
    }
}
