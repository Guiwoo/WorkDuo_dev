package com.workduo.member.membercalendar.repository;

import com.workduo.group.group.entity.Group;
import com.workduo.group.groupmetting.entity.GroupMeeting;
import com.workduo.member.member.entity.Member;
import com.workduo.member.membercalendar.entity.MemberCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberCalendarRepository extends JpaRepository<MemberCalendar, Long> {

    @Modifying
    @Query("update MemberCalendar  mc " +
            "set mc.meetingActiveStatus = 'MEETING_ACTIVE_STATUS_GROUP_LEADER_WITHDRAW' " +
            "where mc.group = :group")
    void updateMemberCalendarMeetingActiveStatusGroupCancel(@Param("group") Group group);

    @Modifying
    @Query("update MemberCalendar  mc " +
            "set mc.meetingActiveStatus = 'MEETING_ACTIVE_STATUS_CANCEL' " +
            "where mc.member = :member")
    void updateMemberCalendarMemberWithdraw(@Param("member") Member member);

    @Modifying
    @Query("update MemberCalendar  mc " +
            "set mc.meetingActiveStatus = 'MEETING_ACTIVE_STATUS_CANCEL' " +
            "where mc.member = :member " +
            "and mc.group = :group")
    void updateMemberCalendarMemberAndGroupWithdraw(
            @Param("member") Member member,
            @Param("group") Group group);

    void deleteByMember(Member m);

    @Query("select mc from MemberCalendar mc " +
            "where mc.member = :member " +
            "and mc.group = :group " +
            "and mc.groupMeeting = :groupMeeting " +
            "and mc.meetingActiveStatus = 'MEETING_ACTIVE_STATUS_ING'")
    Optional<MemberCalendar> findByMemberAndGroupAndGroupMeeting(
            @Param("member") Member member,
            @Param("group") Group group,
            @Param("groupMeeting") GroupMeeting groupMeeting);

    @Modifying
    @Query("update MemberCalendar  mc " +
            "set mc.meetingActiveStatus = 'MEETING_ACTIVE_STATUS_DISMISS' " +
            "where mc.groupMeeting = :groupMeeting ")
    void updateMemberCalendarByGroupMeeting(@Param("groupMeeting") GroupMeeting groupMeeting);

}
