package com.workduo.member.member.repository;

import com.workduo.member.member.entity.MemberInterestedSport;
import com.workduo.member.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InterestedSportRepository extends JpaRepository<MemberInterestedSport,Long> {
    List<MemberInterestedSport> findAllByMember(Member m);
    @Modifying
    @Query("delete from MemberInterestedSport mis where mis.member = :member")
    void deleteByMember(@Param("member") Member m);
}
