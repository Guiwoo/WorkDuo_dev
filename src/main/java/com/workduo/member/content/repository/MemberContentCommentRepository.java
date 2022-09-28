package com.workduo.member.content.repository;

import com.workduo.member.content.entity.MemberContent;
import com.workduo.member.content.entity.MemberContentComment;
import com.workduo.member.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberContentCommentRepository extends JpaRepository<MemberContentComment,Long> {

    Optional<MemberContentComment> findByIdAndMemberAndMemberContentAndDeletedYn(
            Long mcc,Member m, MemberContent mc,boolean deleted);

    Optional<MemberContentComment> findByIdAndAndMemberContentAnAndDeletedYn(Long id,MemberContent mc,boolean dyn);
    List<MemberContentComment> findAllByMemberContent(MemberContent memberContent);
    @Modifying
    @Query("delete from MemberContentComment mcc where mcc.memberContent = :memberContent")
    void deleteAllByMemberContent(@Param("memberContent") MemberContent memberContent);

    @Modifying
    @Query("delete from MemberContentComment mcc where mcc.id = :commentId")
    void deleteById(@Param("commentId") Long commentId);
}
