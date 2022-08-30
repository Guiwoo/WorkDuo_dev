package com.workduo.group.gropcontent.repository;

import com.workduo.group.gropcontent.entity.GroupContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupContentRepository extends JpaRepository<GroupContent, Long> {
}
