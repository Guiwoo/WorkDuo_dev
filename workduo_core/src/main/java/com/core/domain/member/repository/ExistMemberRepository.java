package com.core.domain.member.repository;

import com.core.domain.member.entity.ExistMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExistMemberRepository extends JpaRepository<ExistMember,String> {
    boolean existsByMemberEmail(String email);
    @Modifying
    @Query("delete from ExistMember em where em.memberEmail = :id")
    void deleteById(@Param("id") String id);
}
