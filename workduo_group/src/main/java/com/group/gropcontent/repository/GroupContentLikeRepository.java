package com.group.gropcontent.repository;

import com.core.domain.groupContent.entity.GroupContent;
import com.core.domain.groupContent.entity.GroupContentLike;
import com.core.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupContentLikeRepository extends JpaRepository<GroupContentLike, Long> {
    boolean existsByMemberAndGroupContent(Member member, GroupContent groupContent);
    void deleteByMemberAndGroupContent(Member member, GroupContent groupContent);
}
