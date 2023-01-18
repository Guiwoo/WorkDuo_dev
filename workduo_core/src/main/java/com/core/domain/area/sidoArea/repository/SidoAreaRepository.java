package com.core.domain.area.sidoArea.repository;

import com.core.domain.area.sidoArea.SidoArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SidoAreaRepository extends JpaRepository<SidoArea, String> {
}

