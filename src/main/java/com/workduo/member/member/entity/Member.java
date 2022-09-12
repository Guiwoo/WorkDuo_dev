package com.workduo.member.member.entity;

import com.workduo.configuration.jpa.entitiy.BaseEntity;
import com.workduo.member.member.type.MemberStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "member")
public class Member extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String email; // 유저 이메일 (실제 사용되는 id)
    private String password; // 비밀번호
    private String username; // 유저 이름
    private String nickname; // 별명
    private String phoneNumber; // 핸드폰
    private String status; // 상태메세지

    @Lob
    private String profileImg; // 프로필이미지

    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus;

    private LocalDateTime deletedAt; // 삭제(탈퇴) 날짜

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateUsername(String username) {
        this.username = username;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void updateStatus(String status) {
        this.status = status;
    }

    public void updateProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public void updateMemberStatus(MemberStatus memberStatus) {
        this.memberStatus = memberStatus;
    }
}
