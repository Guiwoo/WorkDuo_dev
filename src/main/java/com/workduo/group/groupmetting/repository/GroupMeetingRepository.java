package com.workduo.group.groupmetting.repository;

import com.workduo.group.groupmetting.entity.GroupMeeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMeetingRepository extends JpaRepository<GroupMeeting, Long> {
}
