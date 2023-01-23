package com.group.gropcontent.repository.query;

import com.core.domain.groupContent.dto.GroupContentCommentDto;
import com.core.domain.groupContent.dto.GroupContentDto;
import com.core.domain.groupContent.dto.GroupContentImageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface GroupContentQueryRepository {

    Page<GroupContentDto> findByGroupContentList(Pageable pageable, Long groupId);
    Optional<GroupContentDto> findByGroupContent(Long groupId, Long groupContentId);
    List<GroupContentImageDto> findByGroupContentImage(Long groupId, Long groupContentId);
    Page<GroupContentCommentDto> findByGroupContentComments(Pageable pageable, Long groupId, Long groupContentId);
}
