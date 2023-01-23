package com.group.gropcontent.repository;

import com.core.domain.groupContent.entity.GroupContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupContentRepository extends JpaRepository<GroupContent, Long> {
}
