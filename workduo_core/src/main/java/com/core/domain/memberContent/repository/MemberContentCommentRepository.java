package com.core.domain.memberContent.repository;

import com.core.domain.member.entity.Member;
import com.core.domain.memberContent.entity.MemberContent;
import com.core.domain.memberContent.entity.MemberContentComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberContentCommentRepository extends JpaRepository<MemberContentComment,Long> {

    Optional<MemberContentComment> findByIdAndMemberAndMemberContentAndDeletedYn(
            Long mcc, Member m, MemberContent mc, boolean deleted);
    Optional<MemberContentComment> findByIdAndMemberContentAndDeletedYn(Long id,MemberContent mc,boolean dyn);
    List<MemberContentComment> findAllByMemberContent(MemberContent memberContent);
    @Modifying
    @Query("delete from MemberContentComment mcc where mcc.memberContent = :memberContent")
    void deleteAllByMemberContent(@Param("memberContent") MemberContent memberContent);

    @Modifying
    @Query("delete from MemberContentComment mcc where mcc.id = :commentId")
    void deleteById(@Param("commentId") Long commentId);
}
