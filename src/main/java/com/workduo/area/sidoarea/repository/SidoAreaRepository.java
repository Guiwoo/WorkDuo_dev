package com.workduo.area.sidoarea.repository;

import com.workduo.area.sidoarea.entity.SidoArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SidoAreaRepository extends JpaRepository<SidoArea, String> {
}
