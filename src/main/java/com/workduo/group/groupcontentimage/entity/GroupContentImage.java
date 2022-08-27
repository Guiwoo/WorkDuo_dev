package com.workduo.group.groupcontentimage.entity;

import com.workduo.group.gropcontent.entity.GroupContent;
import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity(name = "group_content_image")
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
}
