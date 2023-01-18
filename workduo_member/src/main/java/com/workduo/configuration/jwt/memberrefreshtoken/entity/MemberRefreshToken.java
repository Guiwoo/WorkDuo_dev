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
    private String memberEmail;

    private String refreshToken;
    private LocalDateTime expireDate;

    public void updateExpireDate() {
        this.expireDate = LocalDateTime.now().plusYears(1);
    }
}
