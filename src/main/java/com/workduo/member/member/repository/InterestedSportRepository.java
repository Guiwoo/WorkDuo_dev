package com.workduo.member.member.repository;

import com.workduo.member.member.entity.MemberInterestedSport;
import com.workduo.member.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterestedSportRepository extends JpaRepository<MemberInterestedSport,Long> {
    List<MemberInterestedSport> findAllByMember(Member m);
    void deleteByMember(Member m);
}
