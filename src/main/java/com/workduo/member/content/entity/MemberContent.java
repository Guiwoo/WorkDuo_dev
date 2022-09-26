package com.workduo.member.content.entity;

import com.workduo.configuration.jpa.entitiy.BaseEntity;
import com.workduo.member.area.entity.MemberActiveArea;
import com.workduo.member.content.dto.ContentUpdate;
import com.workduo.member.contentimage.entitiy.MemberContentImage;
import com.workduo.member.member.entity.Member;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
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

    @OneToMany(mappedBy = "memberContent",fetch = FetchType.LAZY,cascade = CascadeType.REMOVE)
    private List<MemberContentImage> memberContentImages = new ArrayList<>();

    private String title;

    @Lob
    private String content;

    private boolean noticeYn;

    private int sortValue;

    private boolean deletedYn; // 삭제(탈퇴) 여부
    private LocalDateTime deletedAt; // 삭제(탈퇴) 날짜

    public void updateContent(ContentUpdate.Request req){
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
