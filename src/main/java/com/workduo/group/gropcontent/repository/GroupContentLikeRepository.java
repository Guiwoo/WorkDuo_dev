package com.workduo.group.gropcontent.repository;

import com.workduo.group.gropcontent.entity.GroupContent;
import com.workduo.group.gropcontent.entity.GroupContentLike;
import com.workduo.member.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupContentLikeRepository extends JpaRepository<GroupContentLike, Long> {
    boolean existsByMemberAndGroupContent(Member member, GroupContent groupContent);
    void deleteByMemberAndGroupContent(Member member, GroupContent groupContent);
}
