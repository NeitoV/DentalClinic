package com.java.dental_clinic.repostiory;

import com.java.dental_clinic.data.entity.Objective;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObjectiveRepository extends JpaRepository<Objective, Long> {
    List<Objective> findAllByMedicalRecordId(Long medicalRecordId);

    void deleteAllByMedicalRecordId(Long medicalRecordId);

    @Query("SELECT o FROM Objective o WHERE o.medicalRecord.id = :medicalRecordId AND " +
            "(:objectiveId IS NULL OR o.id = :objectiveId)")
    Page<Objective> findAllByMedicalRecordIdAndObjectiveId(@Param("medicalRecordId") Long medicalRecordId,
                                                           @Param("objectiveId") Long objectiveId, Pageable pageable);

}
