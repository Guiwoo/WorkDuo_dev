package com.workduo.group.gropcontent.repository;

import com.core.domain.groupContent.entity.GroupContent;
import com.core.domain.groupContent.entity.GroupContentComment;
import com.core.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupContentCommentRepository extends JpaRepository<GroupContentComment, Long> {

    @Query("select c " +
            "from GroupContentComment c " +
            "join fetch c.member " +
            "where c.groupContent = :groupContent " +
            "and c.member = :member " +
            "and c.id = :groupContentCommentId")
    Optional<GroupContentComment> findByGroupContentCommentIdGroupContentAndMember(
            @Param("groupContentCommentId") Long groupContentCommentId,
            @Param("groupContent") GroupContent groupContent,
            @Param("member") Member member);

    Optional<GroupContentComment> findByIdAndGroupContent(Long id, GroupContent groupContent);
}
