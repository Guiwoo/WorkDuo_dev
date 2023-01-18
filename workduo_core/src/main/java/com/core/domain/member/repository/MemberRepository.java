package com.core.domain.member.repository;

import com.core.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(@Param("email") String email);
    @Query("select distinct m from Member m join fetch m.memberRoles where m.email = :email")
    Optional<Member> findByEmailOnLoadByUserName(@Param("email") String email);
    boolean existsByNickname(String nickname);
    boolean existsByPhoneNumber(String phoneNumber);
}
