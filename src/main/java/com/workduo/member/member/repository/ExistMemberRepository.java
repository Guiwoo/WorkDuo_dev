package com.workduo.member.member.repository;

import com.workduo.member.member.entity.ExistMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExistMemberRepository extends JpaRepository<ExistMember,String> {
    boolean existsByMemberEmail(String email);
}
