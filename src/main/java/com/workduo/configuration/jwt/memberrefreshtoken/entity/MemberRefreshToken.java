package com.workduo.configuration.jwt.memberrefreshtoken.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "member_refresh_token")
public class MemberRefreshToken {

    @Id
    private String memberId;

    private String refreshToken;
    private LocalDateTime expiredAt;

    public void updateExpireDate() {
        this.expiredAt = LocalDateTime.now().plusYears(1);
    }
}
