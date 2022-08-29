package com.workduo.group.groupmeetingparticipant.repository;

import com.workduo.group.groupmeetingparticipant.entity.GroupMeetingParticipant;
import com.workduo.group.groupmetting.entity.GroupMeeting;
import com.workduo.member.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMeetingParticipantRepository extends JpaRepository<GroupMeetingParticipant, Long> {
    boolean existsByMemberAndGroupMeeting(Member member, GroupMeeting groupMeeting);
    Integer countByGroupMeeting(GroupMeeting groupMeeting);
}
