package com.workduo.group.groupcontentlike.entity;

import com.workduo.group.gropcontent.entity.GroupContent;
import com.workduo.member.member.entity.Member;
import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity(name = "group_content_like")
@Table(name = "group_content_like")
public class GroupContentLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_content_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_content_id")
    private GroupContent groupContent;
}
