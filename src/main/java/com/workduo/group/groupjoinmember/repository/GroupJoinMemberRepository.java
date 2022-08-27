package com.workduo.group.groupjoinmember.repository;

import com.workduo.group.groupjoinmember.entity.GroupJoinMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupJoinMemberRepository extends JpaRepository<GroupJoinMember, Long> {

    @Query("select gjm from GroupJoinMember gjm join fetch gjm.member where gjm.id = :id")
    Optional<GroupJoinMember> findById(Long id);
}
