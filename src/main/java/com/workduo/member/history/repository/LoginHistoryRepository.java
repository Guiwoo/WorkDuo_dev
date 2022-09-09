package com.workduo.member.history.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginHistory extends JpaRepository<com.workduo.member.history.entity.LoginHistory,Long> {
}
