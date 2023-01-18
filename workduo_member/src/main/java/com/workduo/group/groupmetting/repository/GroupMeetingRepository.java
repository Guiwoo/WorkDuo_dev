package com.workduo.group.groupmetting.repository;

import com.core.domain.group.entity.Group;
import com.core.domain.groupMeeting.entity.GroupMeeting;
import com.core.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupMeetingRepository extends JpaRepository<GroupMeeting, Long> {

    Optional<GroupMeeting> findByIdAndGroupAndMember(Long id, Group group, Member member);
    Optional<GroupMeeting> findByIdAndGroup(Long id, Group group);
}
