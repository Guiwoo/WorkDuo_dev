package com.core.domain.memberContent.repository;

import com.core.domain.member.entity.Member;
import com.core.domain.memberContent.entity.MemberContent;
import com.core.domain.memberContent.entity.MemberContentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberContentLikeRepository extends JpaRepository<MemberContentLike,Long> {
    @Modifying
    @Query("delete from MemberContentLike mcl where mcl.memberContent = :contentId")
    void deleteAllByMemberContent(@Param("contentId") MemberContent memberContent);

    boolean existsByMemberAndMemberContent(Member m, MemberContent mc);

    @Modifying
    @Query("delete from MemberContentLike mcl where mcl.memberContent = :contentId and mcl.member = :memberId")
    void deleteByMemberAndMemberContent(@Param("memberId") Member m, @Param("contentId") MemberContent mc);
}
