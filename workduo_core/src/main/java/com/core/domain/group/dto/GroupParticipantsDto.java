package com.core.domain.group.dto;


import com.core.domain.group.entity.GroupJoinMember;
import com.core.domain.group.type.GroupRole;
import com.core.domain.member.entity.Member;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupParticipantsDto {

    private Long userId;
    private String username;
    private String nickname;
    private String profileImg;
    private GroupRole groupRole;

    @QueryProjection
    public GroupParticipantsDto(Member member, GroupJoinMember groupJoinMember) {
        this.userId = member.getId();
        this.username = member.getUsername();
        this.nickname = member.getNickname();
        this.profileImg = member.getProfileImg();
        this.groupRole = groupJoinMember.getGroupRole();
    }
}
