package com.java.dental_clinic.repostiory;

import com.java.dental_clinic.data.entity.Objective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObjectiveRepository extends JpaRepository<Objective, Long> {
    List<Objective> findAllByMedicalRecordId(Long medicalRecordId);

    void deleteAllByMedicalRecordId(Long medicalRecordId);
}
