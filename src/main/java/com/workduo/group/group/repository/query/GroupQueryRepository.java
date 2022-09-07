package com.workduo.group.group.repository.query;

import com.workduo.group.group.dto.GroupDto;

import java.util.Optional;

public interface GroupQueryRepository {
    Optional<GroupDto> findById(Long groupId);
}
