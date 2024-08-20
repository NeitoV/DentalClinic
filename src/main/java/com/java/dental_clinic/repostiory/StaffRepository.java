package com.java.dental_clinic.repostiory;

import com.java.dental_clinic.data.entity.Staff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    @Query("SELECT s FROM Staff s WHERE (:keyword = '' OR s.name LIKE %:keyword% OR s.user.phoneNumber LIKE %:keyword%) " +
            "AND (s.position.id = :positionId OR :positionId = 0)")
    Page<Staff> filter(@Param("keyword") String keyword, Pageable pageable, @Param("positionId") Long positionId);

    Optional<Staff> findByUserId(Long userId);
}
