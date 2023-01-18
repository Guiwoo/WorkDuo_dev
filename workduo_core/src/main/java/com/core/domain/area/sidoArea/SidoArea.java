package com.core.domain.area.sidoArea;

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
@Table(name = "sido_area")
public class SidoArea {

    @Id
    @Column(name = "sido")
    private String sido;

    private String sidonm;
}
