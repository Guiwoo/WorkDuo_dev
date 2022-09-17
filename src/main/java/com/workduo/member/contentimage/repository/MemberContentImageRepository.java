package com.workduo.member.contentimage.repository;

import com.workduo.member.contentimage.entitiy.MemberContentImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberContentImageRepository extends JpaRepository<MemberContentImage,Long> {
}
