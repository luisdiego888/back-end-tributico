package com.project.demo.logic.entity.detailsInvoice;

import jakarta.persistence.*;
import com.project.demo.logic.entity.invoice.Invoice;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "details_invoice")
public class DetailsInvoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cabys;
    private String description;

    private double quantity;
    private double unitPrice;
    private String unit;
    private double discount;
    private double tax;
    private String category;
    private double taxAmount;
    private double total;

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    @JsonBackReference
    private Invoice invoice;

    public DetailsInvoice() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCabys() {
        return cabys;
    }

    public void setCabys(String cabys) {
        this.cabys = cabys;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }
}
