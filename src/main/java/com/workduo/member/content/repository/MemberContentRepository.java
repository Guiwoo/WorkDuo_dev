package com.workduo.member.content.repository;

import com.workduo.member.content.entity.MemberContent;
import com.workduo.member.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberContentRepository extends JpaRepository<MemberContent,Long> {
    boolean existsByMemberAndId(Member m, Long contentId);
}
