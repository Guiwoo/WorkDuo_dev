package com.workduo.area.siggarea.repository;

import com.workduo.area.siggarea.entity.SiggArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiggAreaRepository extends JpaRepository<SiggArea, Integer> {
}
