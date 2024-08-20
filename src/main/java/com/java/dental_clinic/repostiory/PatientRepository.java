package com.java.dental_clinic.repostiory;

import com.java.dental_clinic.data.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    @Query("SELECT p FROM Patient p WHERE p.name LIKE %:keyword% " +
            "OR p.user.phoneNumber LIKE %:keyword%")
    Page<Patient> filter(@Param("keyword") String keyword, Pageable pageable);


    Optional<Patient> findByUserId(long userId);
}
