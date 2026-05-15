package com.project.demo.logic.entity.invoiceUser;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceUserRepository extends JpaRepository<InvoiceUser, Long> {
    InvoiceUser findByIdentification(String identification);
}
