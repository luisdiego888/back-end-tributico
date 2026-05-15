package com.project.demo.logic.entity.fiscalCalendar;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.Date;

@Table(name = "fiscal_calendar")
@Entity
public class FiscalCalendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String description;
    private LocalDate taxDeclarationDeadline;
    private String type;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date created_at;

    @CreationTimestamp
    @Column(updatable = false, name = "updated_at")
    private Date updated_at;

    public FiscalCalendar() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public LocalDate getTaxDeclarationDeadline() {
        return taxDeclarationDeadline;
    }

    public void setTaxDeclarationDeadline(LocalDate taxDeclarationDeadline) {
        this.taxDeclarationDeadline = taxDeclarationDeadline;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
