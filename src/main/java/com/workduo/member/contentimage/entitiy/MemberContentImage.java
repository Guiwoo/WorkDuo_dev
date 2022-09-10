package com.workduo.member.contentimage.entitiy;

import com.workduo.member.content.entity.MemberContent;
import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "member_content_image")
public class MemberContentImage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_content_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_content_id")
    private MemberContent memberContent;

    @Lob
    private String imgPath;
}
