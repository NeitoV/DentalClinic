package com.java.dental_clinic.repostiory;

import com.java.dental_clinic.data.entity.TherapyProcedure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcedureRepository extends JpaRepository<TherapyProcedure, Long> {
    List<TherapyProcedure> findAllByMedicalRecordId(Long medicalRecordId);
}
