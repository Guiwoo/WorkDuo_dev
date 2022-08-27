package com.workduo.member.contentlike.entity;

import com.workduo.member.content.entity.MemberContent;
import com.workduo.member.member.entity.Member;
import lombok.*;

import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class MemberContentLikeId implements Serializable {
    private Member member;
    private MemberContent memberContent;
}
