package com.group.gropcontent.repository;

import com.core.domain.groupContent.entity.GroupContentComment;
import com.core.domain.groupContent.entity.GroupContentCommentLike;
import com.core.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupContentCommentLikeRepository extends JpaRepository<GroupContentCommentLike, Long> {

    boolean existsByGroupContentCommentAndMember(GroupContentComment groupContentComment, Member member);
    void deleteByGroupContentCommentAndMember(GroupContentComment groupContentComment, Member member);

}
