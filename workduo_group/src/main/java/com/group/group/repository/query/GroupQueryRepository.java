package com.group.group.repository.query;

import com.core.domain.group.dto.GroupDto;
import com.core.domain.group.dto.GroupParticipantsDto;
import com.group.group.dto.ListGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface GroupQueryRepository {
    Optional<GroupDto> findById(Long groupId);
    Page<GroupDto> findByGroupList(Pageable pageable,
                                   Long memberId,
                                   ListGroup.Request condition);
    Page<GroupParticipantsDto> findByGroupParticipantList(Pageable pageable, Long groupId);
}
