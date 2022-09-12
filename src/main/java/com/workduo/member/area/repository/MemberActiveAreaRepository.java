package com.workduo.member.area.repository;

import com.workduo.area.siggarea.entity.SiggArea;
import com.workduo.member.area.entity.MemberActiveArea;
import com.workduo.member.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberActiveAreaRepository extends JpaRepository<MemberActiveArea,Long> {
    List<MemberActiveArea> findAllByMember(Member m);
    void deleteByMember(Member m);
}
