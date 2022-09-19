package com.workduo.member.contentimage.entitiy;

import com.workduo.group.gropcontent.entity.GroupContent;
import com.workduo.group.gropcontent.entity.GroupContentImage;
import com.workduo.member.content.entity.MemberContent;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    public static List<MemberContentImage> createMemberContentImage(
            MemberContent memberContent,
            List<String> memberContentImages) {
        List<MemberContentImage> list = new ArrayList<>();
        for (String memberContentImage : memberContentImages) {
            list.add(MemberContentImage.builder()
                    .memberContent(memberContent)
                    .imgPath(memberContentImage)
                    .build());
        }
        return list;
    }
}
