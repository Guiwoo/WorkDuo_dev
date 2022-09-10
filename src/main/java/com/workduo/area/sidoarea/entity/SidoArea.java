package com.workduo.area.sidoarea.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "sido_area")
public class SidoArea {

    @Id
    @Column(name = "sido")
    private String sido;

    private String sidonm;
}
