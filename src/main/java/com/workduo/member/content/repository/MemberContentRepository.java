package com.workduo.member.content.repository;

import com.workduo.member.content.entity.MemberContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberContentRepository extends JpaRepository<MemberContent,Long> {
}
