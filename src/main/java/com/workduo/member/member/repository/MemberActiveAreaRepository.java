package com.workduo.member.member.repository;

import com.workduo.member.member.entity.MemberActiveArea;
import com.workduo.member.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberActiveAreaRepository extends JpaRepository<MemberActiveArea,Long> {
    List<MemberActiveArea> findAllByMember(Member m);
    @Modifying
    @Query("delete from MemberActiveArea maa where maa.member = :member")
    void deleteByMember(@Param("member") Member m);
}
