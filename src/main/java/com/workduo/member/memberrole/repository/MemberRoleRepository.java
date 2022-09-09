package com.workduo.member.memberrole.repository;

import com.workduo.member.member.entity.Member;
import com.workduo.member.memberrole.entity.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRoleRepository extends JpaRepository<MemberRole,Long> {
    List<MemberRole> findByMember(Member m);
}
