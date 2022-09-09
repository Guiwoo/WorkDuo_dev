package com.workduo.group.group.repository.query;

import com.workduo.group.group.dto.GroupDto;
import com.workduo.group.group.dto.GroupParticipantsDto;
import com.workduo.group.group.dto.ListGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface GroupQueryRepository {
    Optional<GroupDto> findById(Long groupId);
    Page<GroupDto> findByGroupList(Pageable pageable, ListGroup.Request condition);
    Page<GroupParticipantsDto> findByGroupParticipantList(Pageable pageable, Long groupId);
}
