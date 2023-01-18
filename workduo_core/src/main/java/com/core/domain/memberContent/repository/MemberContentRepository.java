package com.core.domain.memberContent.repository;

import com.core.domain.member.entity.Member;
import com.core.domain.memberContent.entity.MemberContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberContentRepository extends JpaRepository<MemberContent,Long> {
    boolean existsByMemberAndId(Member m, Long contentId);
}
