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
            "ORDER BY s.date asc")
    List<Schedule> filter(@Param("staffId") Long staffId, @Param("isConfirm") Boolean isConfirm);

    int countByCalendarWorkingId(Long calendarWorkingId);

    List<Schedule> findAllByPatientId(Long patientId);
}
