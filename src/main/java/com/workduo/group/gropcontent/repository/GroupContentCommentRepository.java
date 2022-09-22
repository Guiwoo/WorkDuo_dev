package com.workduo.group.gropcontent.repository;

import com.workduo.group.gropcontent.entity.GroupContent;
import com.workduo.group.gropcontent.entity.GroupContentComment;
import com.workduo.member.member.entity.Member;
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
    Optional<GroupContentComment> findByGroupContentAndMember(
            Long groupContentCommentId,
            GroupContent groupContent,
            Member member);
}
