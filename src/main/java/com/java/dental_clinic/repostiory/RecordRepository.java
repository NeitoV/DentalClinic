package com.java.dental_clinic.repostiory;

import com.java.dental_clinic.data.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<MedicalRecord, Long> {

    List<MedicalRecord> findAllByPatientId(Long patientId);
}
