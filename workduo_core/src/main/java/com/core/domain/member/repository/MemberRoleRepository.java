package com.core.domain.member.repository;

import com.core.domain.member.entity.Member;
import com.core.domain.member.entity.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRoleRepository extends JpaRepository<MemberRole,Long> {
    List<MemberRole> findByMember(Member m);
    @Modifying
    @Query("delete from member_role mr where mr.member = :member")
    void deleteByMember(@Param("member") Member m);
}