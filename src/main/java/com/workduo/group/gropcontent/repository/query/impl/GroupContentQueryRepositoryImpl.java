package com.workduo.group.gropcontent.repository.query.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.workduo.group.gropcontent.entity.GroupContent;
import com.workduo.group.gropcontent.entity.QGroupContent;
import com.workduo.group.gropcontent.entity.QGroupContentImage;
import com.workduo.group.gropcontent.repository.query.GroupContentQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.workduo.group.gropcontent.entity.QGroupContent.groupContent;
import static com.workduo.group.gropcontent.entity.QGroupContentImage.groupContentImage;

@Service
@RequiredArgsConstructor
public class GroupContentQueryRepositoryImpl implements GroupContentQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<GroupContent> findByGroupContent(Long groupContentId) {
//        return Optional.ofNullable(
//                jpaQueryFactory
//                    .select(groupContent).distinct()
//                    .from(groupContent)
//                    .join(groupContent.groupContentImages, groupContentImage).fetchJoin()
//                    .where(groupContent.id.eq(groupContentId))
//                    .fetchOne()
//        );
        return null;
    }
}
