package com.workduo.group.gropcontent.repository.query;

import com.workduo.group.gropcontent.entity.GroupContent;

import java.util.Optional;

public interface GroupContentQueryRepository {

    Optional<GroupContent> findByGroupContent(Long groupContentId);
}
