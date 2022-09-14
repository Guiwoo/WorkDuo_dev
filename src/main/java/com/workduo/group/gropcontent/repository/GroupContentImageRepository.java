package com.workduo.group.gropcontent.repository;

import com.workduo.group.gropcontent.entity.GroupContentImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupContentImageRepository extends JpaRepository<GroupContentImage, Long> {
}
