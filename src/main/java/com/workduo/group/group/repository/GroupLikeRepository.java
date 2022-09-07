package com.workduo.group.group.repository;

import com.workduo.group.group.entity.Group;
import com.workduo.group.group.entity.GroupLike;
import com.workduo.member.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupLikeRepository extends JpaRepository<GroupLike, Long> {

    void deleteByGroupAndMember(Group group, Member member);
}
