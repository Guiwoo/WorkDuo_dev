package com.workduo.group.groupjoinmember.repository;

import com.workduo.group.group.entity.Group;
import com.workduo.group.groupjoinmember.entity.GroupJoinMember;
import com.workduo.member.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface GroupJoinMemberRepository extends JpaRepository<GroupJoinMember, Long> {

    @Modifying
    @Query("update GroupJoinMember gjm " +
            "set gjm.groupJoinMemberStatus = 'GROUP_JOIN_MEMBER_STATUS_CANCEL', " +
            "gjm.deletedAt = current_timestamp " +
            "where gjm.group = :group")
    void updateGroupJoinMemberStatusCancel(@Param("group") Group group);
    Optional<GroupJoinMember> findByMember(Member member);
}
