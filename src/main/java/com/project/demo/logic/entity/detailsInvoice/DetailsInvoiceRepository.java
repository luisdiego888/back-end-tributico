package com.project.demo.logic.entity.detailsInvoice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DetailsInvoiceRepository extends JpaRepository<DetailsInvoice, Long> {
    @Query("SELECT e FROM DetailsInvoice e WHERE " +
            "LOWER(e.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<DetailsInvoice> searchBillsDetails(@Param("search") String search, Pageable pageable);
}
