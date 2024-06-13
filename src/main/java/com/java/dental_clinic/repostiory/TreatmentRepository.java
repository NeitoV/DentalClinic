package com.java.dental_clinic.repostiory;

import com.java.dental_clinic.data.entity.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, Long> {
    @Query("SELECT t FROM Treatment t WHERE " +
            "( :keyword = '' OR t.name LIKE %:keyword%) AND " +
            "( t.service.id = :serviceId OR :serviceId = 0) " +
            "ORDER BY " +
            "CASE WHEN :sortOrder = 'asc' THEN t.cost END ASC, " +
            "CASE WHEN :sortOrder = 'desc' THEN t.cost END DESC, " +
            "CASE WHEN :sortOrder = '' THEN 1 END")
    List<Treatment> filter(@Param("keyword") String keyword, @Param("serviceId") Long serviceId, @Param("sortOrder") String sortOrder);
}
