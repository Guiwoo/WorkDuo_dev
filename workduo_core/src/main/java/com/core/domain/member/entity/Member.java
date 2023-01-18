package com.core.domain.member.entity;

import com.core.domain.base.BaseEntity;
import com.core.domain.member.dto.MemberEdit;
import com.core.domain.member.type.MemberStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @OneToMany(mappedBy = "member",fetch = FetchType.LAZY,cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<MemberRole> memberRoles = new ArrayList<>();

    @OneToMany(mappedBy = "member",fetch = FetchType.LAZY , cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<MemberActiveArea> activeAreas = new ArrayList<>();

    @Lob
    private String profileImg; // 프로필이미지

    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus;

    private LocalDateTime deletedAt; // 삭제(탈퇴) 날짜

    public void updatePassword(String password) {
        this.password = password;
    }
    public void updateUserInfo(MemberEdit.Request edit){
        this.username = edit.getUsername();
        this.nickname = edit.getNickname();
        this.phoneNumber = edit.getPhoneNumber();
        this.status = edit.getStatus();
    }
    public void terminate(){
        this.email = "";
        this.username = "";
        this.password = "";
        this.status = "";
        this.nickname = "";
        this.phoneNumber = "";
        this.profileImg = "";
        this.memberStatus = MemberStatus.MEMBER_STATUS_WITHDRAW;
        this.deletedAt = LocalDateTime.now();
    }
    public void updateImage(String imgPath) {
        this.profileImg = imgPath;
    }
}
