package com.workduo.area.siggarea.repository;

import com.workduo.area.siggarea.entity.SiggArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SiggAreaRepository extends JpaRepository<SiggArea, String> {
    Optional<SiggArea> findBySgg(String sgg);
    boolean existsBySgg(String sgg);
}
