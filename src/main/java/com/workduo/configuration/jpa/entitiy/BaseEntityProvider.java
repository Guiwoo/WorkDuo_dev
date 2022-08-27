package com.workduo.configuration.jpa.entitiy;

import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntityProvider extends BaseEntity {

    // 등록자, 등록자는 한번 등록하면 수정하지 못 하게 updatable false로 지정
    @CreatedBy
    @Column(updatable = false)
    private String createBy;

    // 수정자
    @LastModifiedBy
    private String lastModifiedBy;
}
