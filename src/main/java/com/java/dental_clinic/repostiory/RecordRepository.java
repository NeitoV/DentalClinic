package com.java.dental_clinic.repostiory;

import com.java.dental_clinic.data.entity.MedicalRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<MedicalRecord, Long> {

    Page<MedicalRecord> findAllByPatientId(Long patientId, Pageable pageable);
}
