package com.java.dental_clinic.repostiory;

import com.java.dental_clinic.data.entity.TherapyProcedure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ProcedureRepository extends JpaRepository<TherapyProcedure, Long> {

    List<TherapyProcedure> findAllByObjectiveId(Long objectiveId);
    void deleteAllByObjectiveId(Long objectiveId);

    @Transactional
    @Modifying
    @Query("DELETE FROM TherapyProcedure tp WHERE tp.objective.id" +
            " IN (SELECT o.id FROM Objective o WHERE o.medicalRecord.id = :medicalRecordId)")
    void deleteByMedicalRecordId(Long medicalRecordId);

    boolean existsByTreatmentId(Long treatmentId);
}
