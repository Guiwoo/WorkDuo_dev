package com.workduo.group.group.dto;


import com.querydsl.core.annotations.QueryProjection;
import com.workduo.group.group.entity.GroupJoinMember;
import com.workduo.group.group.type.GroupRole;
import com.workduo.member.member.entity.Member;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
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
