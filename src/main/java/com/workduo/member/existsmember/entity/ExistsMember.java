package com.workduo.member.existsmember.entity;

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
@Table(name = "exists_member")
public class ExistsMember {

    @Id
    @Column(name = "member_email")
    private String memberEmail;
}
