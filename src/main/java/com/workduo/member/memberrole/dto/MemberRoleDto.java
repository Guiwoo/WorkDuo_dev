package com.workduo.member.memberrole.dto;

import com.workduo.member.memberrole.type.MemberRoleType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRoleDto {
    private MemberRoleType role;

    @Builder
    public MemberRoleDto(MemberRoleType role) {
        this.role = role;
    }
}
