package com.workduo.member.member.repository;

import com.workduo.member.member.entity.MemberActiveArea;
import com.workduo.member.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberActiveAreaRepository extends JpaRepository<MemberActiveArea,Long> {
    List<MemberActiveArea> findAllByMember(Member m);
    void deleteByMember(Member m);
}
