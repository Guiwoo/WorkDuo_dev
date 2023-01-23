package com.group.gropcontent.repository;

import com.core.domain.groupContent.entity.GroupContentImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupContentImageRepository extends JpaRepository<GroupContentImage, Long> {
}
