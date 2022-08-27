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

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sido_area_id")
    private Integer id;

    private String admCd;
    private String sidonm;
}
