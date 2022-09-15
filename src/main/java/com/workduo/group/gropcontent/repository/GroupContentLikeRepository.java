package com.workduo.group.gropcontent.repository;

import com.workduo.group.gropcontent.entity.GroupContentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupContentLikeRepository extends JpaRepository<GroupContentLike, Long> {
}
