package com.workduo.group.groupmetting.repository;

import com.workduo.group.group.entity.Group;
import com.workduo.group.groupmetting.entity.GroupMeetingParticipant;
import com.workduo.group.groupmetting.entity.GroupMeeting;
import com.workduo.member.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMeetingParticipantRepository extends JpaRepository<GroupMeetingParticipant, Long> {

    @Modifying
    @Query("delete from GroupMeetingParticipant gmp where gmp.group = :group")
    void deleteAllByGroup(Group group);
    void deleteByMember(Member member);
    void deleteByGroupAndMember(Group group, Member member);
    Integer countByGroupMeetingAndGroup(GroupMeeting groupMeeting, Group group);
    @Modifying
    @Query("delete from GroupMeetingParticipant gmp " +
            "where gmp.group = :group " +
            "and gmp.groupMeeting = :groupMeeting")
    void deleteAllByGroupAndGroupMeeting(Group group, GroupMeeting groupMeeting);
}
