package com.core.domain.area.siggArea.repository;

import com.core.domain.area.siggArea.SiggArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SiggAreaRepository extends JpaRepository<SiggArea, String> {
    Optional<SiggArea> findBySgg(String sgg);
    boolean existsBySgg(String sgg);
}
