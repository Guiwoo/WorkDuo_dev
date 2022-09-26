package com.workduo.member.content.repository;

import com.workduo.member.content.entity.MemberContent;
import com.workduo.member.content.entity.MemberContentComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberContentCommentRepository extends JpaRepository<MemberContentComment,Long> {
    List<MemberContentComment> findAllByMemberContent(MemberContent memberContent);
    @Modifying
    @Query("delete from MemberContentComment mcc where mcc.memberContent = :memberContent")
    void deleteAllByMemberContent(@Param("memberContent") MemberContent memberContent);
}
