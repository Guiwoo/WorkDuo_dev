package com.workduo.area.siggarea.entity;

import com.workduo.area.sidoarea.entity.SidoArea;
import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "sigg_area")
public class SiggArea {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sigg_area_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sido_area_id")
    private SidoArea sidoArea;

    private Integer cityId;
    private String amdCd;
    private String sggnm;
}
