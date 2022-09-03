package com.workduo.configuration.jwt.memberrefreshtoken.repository;

import com.workduo.configuration.jwt.memberrefreshtoken.entity.MemberRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRefreshTokenRepository extends JpaRepository<MemberRefreshToken, String> {

    Optional<MemberRefreshToken> findByMemberId(String memberId);
}
