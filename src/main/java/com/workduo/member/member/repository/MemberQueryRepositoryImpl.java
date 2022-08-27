package com.workduo.member.member.repository;

import com.workduo.member.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepositoryImpl implements MemberQueryRepository {

    private final EntityManager em;

    @Override
    public List<Member> findAll() {
        return em.createQuery("select m from Member m where m.memberStatus = 'MEMBER_STATUS_ING'", Member.class)
                .getResultList();
    }
}
