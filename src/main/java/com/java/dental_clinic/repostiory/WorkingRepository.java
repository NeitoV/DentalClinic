package com.java.dental_clinic.repostiory;

import com.java.dental_clinic.data.entity.CalendarWorking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkingRepository extends JpaRepository<CalendarWorking, Long> {
    @Query("SELECT cw FROM CalendarWorking cw " +
            "JOIN cw.staff s " +
            "JOIN cw.period p " +
            "WHERE (:date IS NULL OR cw.date = :date) " +
            "AND (:staffName = '' OR s.name LIKE %:staffName%) " +
            "AND ( p.id = :periodId OR :periodId = 0)")
    List<CalendarWorking> filter(
            @Param("date") LocalDate date,
            @Param("staffName") String staffName,
            @Param("periodId") Long periodId);

    boolean existsByPeriodIdAndDateAndStaffId(Long periodId, LocalDate date, Long staffId);

    @Query("SELECT c FROM CalendarWorking c WHERE c.date BETWEEN :startDate AND :endDate AND c.staff.id = :staffId")
    List<CalendarWorking> findByDateRangeAndStaffId(@Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate,
                                                    @Param("staffId") Long staffId);

    Optional<CalendarWorking> findByPeriodIdAndStaffIdAndDate(Long periodId, Long staffId, LocalDate date);
}
