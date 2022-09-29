package com.workduo.member.member.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "exist_member")
public class ExistMember {

    @Id
    @Column(name = "member_email")
    private String memberEmail;
}
