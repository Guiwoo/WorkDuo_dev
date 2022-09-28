package com.workduo.group.groupmetting.repository;

import com.workduo.group.group.entity.Group;
import com.workduo.group.groupmetting.entity.GroupMeeting;
import com.workduo.member.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupMeetingRepository extends JpaRepository<GroupMeeting, Long> {

    Optional<GroupMeeting> findByIdAndGroupAndMember(Long id, Group group, Member member);
}
