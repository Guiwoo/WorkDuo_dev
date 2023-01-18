package com.core.domain.sport.sport.repository;

import com.core.domain.sport.sport.Sport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SportRepository extends JpaRepository<Sport, Integer> {
}
