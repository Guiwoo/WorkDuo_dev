package com.workduo.member.content.repository;

import com.workduo.member.content.entity.MemberContentImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberContentImageRepository extends JpaRepository<MemberContentImage,Long> {
}
