package com.workduo.member.member.dto.authDto;

import com.workduo.member.member.type.MemberRoleType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberRoleAuthDto {
    private Long id;
    private MemberRoleType role;

    @Builder
    public MemberRoleAuthDto(Long id, MemberRoleType role) {
        this.id = id;
        this.role = role;
    }
}
