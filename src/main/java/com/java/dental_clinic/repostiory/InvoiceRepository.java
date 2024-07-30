package com.java.dental_clinic.repostiory;

import com.java.dental_clinic.data.entity.Invoice;
import io.swagger.models.auth.In;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByObjectiveId(Long objectiveId);

//    @Query("SELECT i FROM Invoice i WHERE i.date >= :startDate AND i.date <= :endDate " +
//            "AND ( i.objective.medicalRecord.staff.id = :staffId OR :staffId = 0 )")
//    List<Invoice> findRevenue(@Param("startDate") LocalDate startDate,
//                              @Param("endDate") LocalDate endDate,
//                              @Param("staffId") Long staffId);

    @Query("SELECT i.date, SUM(i.amountPaid), SUM(i.amountRemaining) FROM Invoice i WHERE " +
            "(i.date BETWEEN :startDate AND :endDate) " +
            "AND (i.objective.medicalRecord.staff.id = :staffId OR :staffId = 0) " +
            "GROUP BY i.date " +
            "ORDER BY i.date asc")
    List<Object[]> findTotalAmountByDateRange(@Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate,
                                              @Param("staffId") Long staffId);

    @Query("SELECT FUNCTION('WEEK', i.date), SUM(i.amountPaid), SUM(i.amountRemaining) " +
            "FROM Invoice i " +
            "WHERE YEAR(i.date) = :year AND MONTH(i.date) = :month " +
            "AND (i.objective.medicalRecord.staff.id = :staffId OR :staffId = 0) " +
            "GROUP BY FUNCTION('WEEK', i.date) " +
            "ORDER BY FUNCTION('WEEK', i.date) asc")
    List<Object[]> findTotalRevenueByWeekInMonth(@Param("year") int year,
                                                 @Param("month") int month,
                                                 @Param("staffId") Long staffId);

    @Query("SELECT MONTH(i.date), SUM(i.amountPaid), SUM(i.amountRemaining)  " +
            "FROM Invoice i " +
            "WHERE YEAR(i.date) = :year " +
            "AND (i.objective.medicalRecord.staff.id = :staffId OR :staffId = 0) " +
            "GROUP BY MONTH(i.date)" +
            "ORDER BY MONTH(i.date) asc")
    List<Object[]> findTotalRevenueByMonthInYear(@Param("year") int year,
                                                 @Param("staffId") Long staffId);
}
