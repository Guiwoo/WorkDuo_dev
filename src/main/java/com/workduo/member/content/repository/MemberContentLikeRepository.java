package com.workduo.member.content.repository;

import com.workduo.member.content.entity.MemberContent;
import com.workduo.member.content.entity.MemberContentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberContentLikeRepository extends JpaRepository<MemberContentLike,Long> {
    @Modifying
    @Query("delete from MemberContentLike mcl where mcl.memberContent = :contentId")
    void deleteAllByMemberContent(@Param("contentId") MemberContent memberContent);
}
