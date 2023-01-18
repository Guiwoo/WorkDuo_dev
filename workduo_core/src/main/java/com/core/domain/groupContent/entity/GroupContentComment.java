package com.core.domain.groupContent.entity;

import com.core.domain.base.BaseEntity;
import com.core.domain.member.entity.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "group_content_comment")
public class GroupContentComment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_content_comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_content_id")
    private GroupContent groupContent;

    @Lob
    private String comment;

    private boolean deletedYn; // 삭제(탈퇴) 여부
    private LocalDateTime deletedAt; // 삭제(탈퇴) 날짜

    public void updateComment(String comment) {
        this.comment = comment;
    }

    public void deleteComment() {
        this.deletedYn = true;
        this.deletedAt = LocalDateTime.now();
    }
}
