package com.core.domain.groupContent.entity;

import com.core.domain.base.BaseEntity;
import com.core.domain.group.entity.Group;
import com.core.domain.groupContent.dto.UpdateGroupContent;
import com.core.domain.member.entity.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "group_content")
public class GroupContent extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_content_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Builder.Default
    @OneToMany(mappedBy = "groupContent", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<GroupContentImage> groupContentImages = new ArrayList<>();

    private String title;

    @Lob
    private String content;

    private boolean deletedYn; // 삭제(탈퇴) 여부
    private LocalDateTime deletedAt; // 삭제(탈퇴) 날짜

    private boolean noticeYn;
    private int sortValue;

    public void deleteContent() {
        this.deletedYn = true;
        this.deletedAt = LocalDateTime.now();
    }

    public void updateContent(UpdateGroupContent.Request request) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.noticeYn = request.isNoticeYn();
        this.sortValue = request.getSortValue();
    }
}
