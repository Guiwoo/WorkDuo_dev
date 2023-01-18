package com.core.domain.memberContent.entity;

import com.core.domain.base.BaseEntity;
import com.core.domain.member.entity.Member;
import com.core.domain.memberContent.dto.ContentMemberUpdate;
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
@Table(name = "member_content")
public class MemberContent extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_content_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder.Default
    @OneToMany(mappedBy = "memberContent",fetch = FetchType.LAZY,cascade = CascadeType.REMOVE)
    private List<MemberContentImage> memberContentImages = new ArrayList<>();

    private String title;

    @Lob
    private String content;

    private boolean noticeYn;

    private int sortValue;

    private boolean deletedYn; // 삭제(탈퇴) 여부
    private LocalDateTime deletedAt; // 삭제(탈퇴) 날짜

    public void updateContent(ContentMemberUpdate.Request req){
        this.title = req.getTitle();
        this.content = req.getContent();
        this.noticeYn = req.isNoticeYn();
        this.sortValue = req.getSortValue();
    }

    public void terminate(){
        this.title = "";
        this.content = "";
        this.deletedYn =true;
        this.deletedAt = LocalDateTime.now();
    }
}
