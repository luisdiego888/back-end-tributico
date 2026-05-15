package com.project.demo.logic.entity.fiscalCalendar;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface FiscalCalendarRepository extends JpaRepository<FiscalCalendar, Long> {

    @Query("SELECT n FROM FiscalCalendar n WHERE " +
            "LOWER(CAST(n.id AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(CAST(n.name AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(n.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<FiscalCalendar> searchFiscalCalendar(@Param("search") String search, Pageable pageable);
    List<FiscalCalendar> findByTaxDeclarationDeadlineBetween(LocalDate startDate, LocalDate endDate);

}
