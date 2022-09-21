package com.workduo.group.gropcontent.repository;

import com.workduo.group.gropcontent.entity.GroupContentComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupContentCommentRepository extends JpaRepository<GroupContentComment, Long> {
}
