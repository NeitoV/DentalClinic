package com.java.dental_clinic.repostiory;

import com.java.dental_clinic.data.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("SELECT s FROM Schedule s " +
            "LEFT JOIN s.calendarWorking cw " +
            "WHERE ( cw.staff.id = :staffId OR :staffId = 0) " +
            "AND (s.isConfirm = :isConfirm OR :isConfirm is null)" +
            "ORDER BY s.date desc")
    List<Schedule> filter(@Param("staffId") Long staffId, @Param("isConfirm") Boolean isConfirm);

    @Query("SELECT COUNT(s) FROM Schedule s WHERE s.calendarWorking.id = :calendarWorkingId AND s.isConfirm = true")
    int countByCalendarWorkingIdAndConfirmTrue(@Param("calendarWorkingId") Long calendarWorkingId);

    @Query("SELECT s FROM Schedule s " +
            "where s.isConfirm = :isConfirm OR :isConfirm is null " +
            "and s.patient.id = :patientId")
    List<Schedule> findForPatient(@Param("patientId") Long patientId, @Param("isConfirm") Boolean isConfirm);
}
