package com.group.group.repository;

import com.core.domain.group.entity.Group;
import com.core.domain.group.entity.GroupLike;
import com.core.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupLikeRepository extends JpaRepository<GroupLike, Long> {

    boolean existsByGroupAndMember(Group group, Member member);
    void deleteByGroupAndMember(Group group, Member member);
}
