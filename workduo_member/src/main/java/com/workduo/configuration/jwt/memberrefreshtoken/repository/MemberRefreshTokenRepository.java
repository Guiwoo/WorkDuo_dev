package com.workduo.configuration.jwt.memberrefreshtoken.repository;

import com.workduo.configuration.jwt.memberrefreshtoken.entity.MemberRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRefreshTokenRepository extends JpaRepository<MemberRefreshToken, String> {

    Optional<MemberRefreshToken> findByMemberEmail(String memberId);

    @Modifying
    @Query("delete from MemberRefreshToken mrt where mrt.memberEmail = :id")
    void deleteById(@Param("id") String id);
}
