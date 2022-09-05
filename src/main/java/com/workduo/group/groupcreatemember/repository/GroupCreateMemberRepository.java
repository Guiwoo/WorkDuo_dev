package com.workduo.group.groupcreatemember.repository;

import com.workduo.group.groupcreatemember.entity.GroupCreateMember;
import com.workduo.group.groupcreatemember.entity.GroupCreateMemberId;
import com.workduo.member.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupCreateMemberRepository extends JpaRepository<GroupCreateMember, GroupCreateMemberId> {
    Long countByMember(Member member);
}
