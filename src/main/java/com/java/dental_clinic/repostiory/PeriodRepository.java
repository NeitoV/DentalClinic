package com.java.dental_clinic.repostiory;

import com.java.dental_clinic.data.entity.Period;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeriodRepository extends JpaRepository<Period, Long> {
    boolean existsByName(String name);
}
