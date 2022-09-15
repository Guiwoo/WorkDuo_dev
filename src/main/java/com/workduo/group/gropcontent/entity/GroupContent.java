package com.workduo.group.gropcontent.entity;

import com.workduo.configuration.jpa.entitiy.BaseEntity;
import com.workduo.group.group.entity.Group;
import com.workduo.member.member.entity.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

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

//    @OneToMany(mappedBy = "groupContent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private List<GroupContentImage> groupContentImages = new ArrayList<>();

    private String title;

    @Lob
    private String content;

    private boolean deletedYn; // 삭제(탈퇴) 여부
    private LocalDateTime deletedAt; // 삭제(탈퇴) 날짜

    private boolean noticeYn;
    private int sortValue;

//    public void addGroupContentImage(GroupContentImage groupContentImage) {
//        this.groupContentImages.add(groupContentImage);
//        groupContentImage.addGroupContent(this);
//    }

//    public static GroupContent createContent(
//            CreateGroupContent.Request request,
//            Group group,
//            Member member,
//            List<GroupContentImage> groupContentImages) {
//
//        GroupContent groupContent = GroupContent.builder()
//                .title(request.getTitle())
//                .content(request.getContent())
//                .deletedYn(false)
//                .group(group)
//                .member(member)
//                .groupContentImages(new ArrayList<>())
//                .noticeYn(request.isNoticeYn())
//                .sortValue(request.getSortValue())
//                .build();
//
//        for (GroupContentImage groupContentImage : groupContentImages) {
//            groupContent.addGroupContentImage(groupContentImage);
//        }
//
//        return groupContent;
//    }
}
