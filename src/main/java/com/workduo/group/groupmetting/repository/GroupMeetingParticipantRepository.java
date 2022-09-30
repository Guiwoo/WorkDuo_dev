package com.workduo.group.groupmetting.repository;

import com.workduo.group.group.entity.Group;
import com.workduo.group.groupmetting.entity.GroupMeetingParticipant;
import com.workduo.group.groupmetting.entity.GroupMeeting;
import com.workduo.member.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMeetingParticipantRepository extends JpaRepository<GroupMeetingParticipant, Long> {

    @Modifying
    @Query("delete from GroupMeetingParticipant gmp where gmp.group = :group")
    void deleteAllByGroup(@Param("group") Group group);
    @Modifying
    @Query("delete from GroupMeetingParticipant gmp where gmp.member = :member")
    void deleteByMember(@Param("member") Member member);
    void deleteByGroupAndMember(Group group, Member member);
    Integer countByGroupMeetingAndGroup(GroupMeeting groupMeeting, Group group);

    @Modifying
    @Query("delete from GroupMeetingParticipant gmp " +
            "where gmp.group = :group " +
            "and gmp.groupMeeting = :groupMeeting")
    void deleteAllByGroupAndGroupMeeting(
            @Param("group") Group group, @Param("groupMeeting") GroupMeeting groupMeeting);

    boolean existsByMemberAndGroupAndGroupMeeting(Member member, Group group, GroupMeeting groupMeeting);

    @Modifying
    @Query("delete from GroupMeetingParticipant gmp " +
            "where gmp.member = :member " +
            "and gmp.group = :group " +
            "and gmp.groupMeeting = :groupMeeting")
    void deleteByMemberAndGroupAndGroupMeeting(
            @Param("member") Member member,
            @Param("group") Group group,
            @Param("groupMeeting") GroupMeeting groupMeeting);

    @Query(value = "select gmp from GroupMeetingParticipant gmp " +
            "join fetch gmp.member " +
            "where gmp.group = :group " +
            "and gmp.groupMeeting = :groupMeeting",
            countQuery = "select count(gmp) from GroupMeetingParticipant gmp " +
                    "where gmp.group = :group " +
                    "and gmp.groupMeeting = :groupMeeting")
    Page<GroupMeetingParticipant> findByGroupAndGroupMeeting(
            @Param("group") Group group,
            @Param("groupMeeting") GroupMeeting groupMeeting,
            Pageable pageable);
}
