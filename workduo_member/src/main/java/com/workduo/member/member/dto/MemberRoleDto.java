package com.workduo.member.member.dto;

import com.core.domain.member.type.MemberRoleType;
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
