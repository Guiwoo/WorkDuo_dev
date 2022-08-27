package com.workduo.member.member.repository;

import com.workduo.member.member.entity.Member;

import java.util.List;

public interface MemberQueryRepository {

    List<Member> findAll();
}
