package com.workduo.group.gropcontent.repository;

import com.workduo.group.gropcontent.entity.GroupContentComment;
import com.workduo.group.gropcontent.entity.GroupContentCommentLike;
import com.workduo.member.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupContentCommentLikeRepository extends JpaRepository<GroupContentCommentLike, Long> {

    boolean existsByGroupContentCommentAndMember(GroupContentComment groupContentComment, Member member);
    void deleteByGroupContentCommentAndMember(GroupContentComment groupContentComment, Member member);

}
