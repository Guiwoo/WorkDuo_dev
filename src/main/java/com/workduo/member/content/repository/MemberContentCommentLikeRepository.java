package com.workduo.member.content.repository;

import com.workduo.member.content.entity.MemberContentComment;
import com.workduo.member.content.entity.MemberContentCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface MemberContentCommentLikeRepository
        extends JpaRepository<MemberContentCommentLike,Long> {
    @Modifying
    @Query("delete from MemberContentCommentLike mccl where mccl.memberContentComment in :comments")
    void deleteAllByMemberContentCommentIn(
            @Param("comments")
            List<MemberContentComment> memberContentComment);
}
