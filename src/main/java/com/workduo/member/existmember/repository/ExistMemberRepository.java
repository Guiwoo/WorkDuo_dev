package com.workduo.member.existmember.repository;

import com.workduo.member.existmember.entity.ExistMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExistMemberRepository extends JpaRepository<ExistMember,String> {
    boolean existsByMemberEmail(String email);
}
