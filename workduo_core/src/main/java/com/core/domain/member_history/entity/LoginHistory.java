package com.core.domain.member_history.entity;

import com.core.domain.member.entity.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "member_login_history")
public class LoginHistory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_history_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime loginDt;
    private String clientIp;
    private String userAgent;
}
