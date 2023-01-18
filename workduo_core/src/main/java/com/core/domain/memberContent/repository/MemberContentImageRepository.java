package com.core.domain.memberContent.repository;

import com.core.domain.memberContent.entity.MemberContentImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberContentImageRepository extends JpaRepository<MemberContentImage,Long> {
}
