package com.core.domain.groupContent.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "group_content_image")
public class GroupContentImage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_content_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_content_id")
    private GroupContent groupContent;

    @Lob
    private String imagePath;

    public void addGroupContent(GroupContent groupContent) {
        this.groupContent = groupContent;
    }

    public static List<GroupContentImage> createGroupContentImage(
            GroupContent groupContent,
            List<String> groupContentImages) {
        List<GroupContentImage> groupContentImageList = new ArrayList<>();
        for (String groupContentImage : groupContentImages) {
            groupContentImageList.add(GroupContentImage.builder()
                    .groupContent(groupContent)
                    .imagePath(groupContentImage)
                    .build());
        }

        return groupContentImageList;
    }
}
