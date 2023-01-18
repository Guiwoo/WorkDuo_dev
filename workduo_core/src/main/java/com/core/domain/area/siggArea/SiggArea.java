package com.core.domain.area.siggArea;

import com.core.domain.area.sidoArea.SidoArea;
import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "sigg_area")
public class SiggArea {

    @Id
    @Column(name = "sgg")
    private String sgg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sido")
    private SidoArea sidoArea;

    private String sidonm;
    private String sggnm;
}
