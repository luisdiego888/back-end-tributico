package com.project.demo.logic.entity.ivaCalculation;

import com.project.demo.logic.entity.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "iva_calculation")
public class IvaCalculation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "year", nullable = false)
    private int year;

    @Column(name = "month", nullable = false)
    private int month;

    @Column(name = "calculation_date")
    private LocalDate calculationDate;

    @Column(precision = 15, scale = 2, name = "iva_ventas_bienes")
    private BigDecimal ivaVentasBienes = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, name = "iva_ventas_servicios")
    private BigDecimal ivaVentasServicios = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, name = "iva_exportaciones")
    private BigDecimal ivaExportaciones = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, name = "iva_actividades_agropecuarias")
    private BigDecimal ivaActividadesAgropecuarias = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, name = "iva_compras_bienes")
    private BigDecimal ivaComprasBienes = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, name = "iva_compras_servicios")
    private BigDecimal ivaComprasServicios = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, name = "iva_importaciones")
    private BigDecimal ivaImportaciones = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, name = "iva_gastos_generales")
    private BigDecimal ivaGastosGenerales = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, name = "iva_activos_fijos")
    private BigDecimal ivaActivosFijos = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, name = "total_iva_debito")
    private BigDecimal totalIvaDebito = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, name = "total_iva_credito")
    private BigDecimal totalIvaCredito = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, name = "iva_neto_por_pagar")
    private BigDecimal ivaNetoPorPagar = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, name = "iva_a_favor")
    private BigDecimal ivaAFavor = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, name = "iva_1_percent")
    private BigDecimal iva1Percent = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, name = "iva_2_percent")
    private BigDecimal iva2Percent = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, name = "iva_4_percent")
    private BigDecimal iva4Percent = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, name = "iva_8_percent")
    private BigDecimal iva8Percent = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, name = "iva_10_percent")
    private BigDecimal iva10Percent = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, name = "iva_13_percent")
    private BigDecimal iva13Percent = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, name = "iva_exento")
    private BigDecimal ivaExento = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    public IvaCalculation() {}

    public IvaCalculation(User user, int year, int month) {
        this.user = user;
        this.year = year;
        this.month = month;
        this.calculationDate = LocalDate.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public LocalDate getCalculationDate() { return calculationDate; }
    public void setCalculationDate(LocalDate calculationDate) { this.calculationDate = calculationDate; }

    public BigDecimal getIvaVentasBienes() { return ivaVentasBienes; }
    public void setIvaVentasBienes(BigDecimal ivaVentasBienes) {
        this.ivaVentasBienes = ivaVentasBienes != null ? ivaVentasBienes : BigDecimal.ZERO;
    }

    public BigDecimal getIvaVentasServicios() { return ivaVentasServicios; }
    public void setIvaVentasServicios(BigDecimal ivaVentasServicios) {
        this.ivaVentasServicios = ivaVentasServicios != null ? ivaVentasServicios : BigDecimal.ZERO;
    }

    public BigDecimal getIvaExportaciones() { return ivaExportaciones; }
    public void setIvaExportaciones(BigDecimal ivaExportaciones) {
        this.ivaExportaciones = ivaExportaciones != null ? ivaExportaciones : BigDecimal.ZERO;
    }

    public BigDecimal getIvaActividadesAgropecuarias() { return ivaActividadesAgropecuarias; }
    public void setIvaActividadesAgropecuarias(BigDecimal ivaActividadesAgropecuarias) {
        this.ivaActividadesAgropecuarias = ivaActividadesAgropecuarias != null ? ivaActividadesAgropecuarias : BigDecimal.ZERO;
    }

    public BigDecimal getIvaComprasBienes() { return ivaComprasBienes; }
    public void setIvaComprasBienes(BigDecimal ivaComprasBienes) {
        this.ivaComprasBienes = ivaComprasBienes != null ? ivaComprasBienes : BigDecimal.ZERO;
    }

    public BigDecimal getIvaComprasServicios() { return ivaComprasServicios; }
    public void setIvaComprasServicios(BigDecimal ivaComprasServicios) {
        this.ivaComprasServicios = ivaComprasServicios != null ? ivaComprasServicios : BigDecimal.ZERO;
    }

    public BigDecimal getIvaImportaciones() { return ivaImportaciones; }
    public void setIvaImportaciones(BigDecimal ivaImportaciones) {
        this.ivaImportaciones = ivaImportaciones != null ? ivaImportaciones : BigDecimal.ZERO;
    }

    public BigDecimal getIvaGastosGenerales() { return ivaGastosGenerales; }
    public void setIvaGastosGenerales(BigDecimal ivaGastosGenerales) {
        this.ivaGastosGenerales = ivaGastosGenerales != null ? ivaGastosGenerales : BigDecimal.ZERO;
    }

    public BigDecimal getIvaActivosFijos() { return ivaActivosFijos; }
    public void setIvaActivosFijos(BigDecimal ivaActivosFijos) {
        this.ivaActivosFijos = ivaActivosFijos != null ? ivaActivosFijos : BigDecimal.ZERO;
    }

    public BigDecimal getTotalIvaDebito() { return totalIvaDebito; }
    public void setTotalIvaDebito(BigDecimal totalIvaDebito) {
        this.totalIvaDebito = totalIvaDebito != null ? totalIvaDebito : BigDecimal.ZERO;
    }

    public BigDecimal getTotalIvaCredito() { return totalIvaCredito; }
    public void setTotalIvaCredito(BigDecimal totalIvaCredito) {
        this.totalIvaCredito = totalIvaCredito != null ? totalIvaCredito : BigDecimal.ZERO;
    }

    public BigDecimal getIvaNetoPorPagar() { return ivaNetoPorPagar; }
    public void setIvaNetoPorPagar(BigDecimal ivaNetoPorPagar) {
        this.ivaNetoPorPagar = ivaNetoPorPagar != null ? ivaNetoPorPagar : BigDecimal.ZERO;
    }

    public BigDecimal getIvaAFavor() { return ivaAFavor; }
    public void setIvaAFavor(BigDecimal ivaAFavor) {
        this.ivaAFavor = ivaAFavor != null ? ivaAFavor : BigDecimal.ZERO;
    }



    public BigDecimal getIva1Percent() { return iva1Percent; }
    public void setIva1Percent(BigDecimal iva1Percent) {
        this.iva1Percent = iva1Percent != null ? iva1Percent : BigDecimal.ZERO;
    }

    public BigDecimal getIva2Percent() { return iva2Percent; }
    public void setIva2Percent(BigDecimal iva2Percent) {
        this.iva2Percent = iva2Percent != null ? iva2Percent : BigDecimal.ZERO;
    }

    public BigDecimal getIva4Percent() { return iva4Percent; }
    public void setIva4Percent(BigDecimal iva4Percent) {
        this.iva4Percent = iva4Percent != null ? iva4Percent : BigDecimal.ZERO;
    }

    public BigDecimal getIva8Percent() { return iva8Percent; }
    public void setIva8Percent(BigDecimal iva8Percent) {
        this.iva8Percent = iva8Percent != null ? iva8Percent : BigDecimal.ZERO;
    }

    public BigDecimal getIva10Percent() { return iva10Percent; }
    public void setIva10Percent(BigDecimal iva10Percent) {
        this.iva10Percent = iva10Percent != null ? iva10Percent : BigDecimal.ZERO;
    }

    public BigDecimal getIva13Percent() { return iva13Percent; }
    public void setIva13Percent(BigDecimal iva13Percent) {
        this.iva13Percent = iva13Percent != null ? iva13Percent : BigDecimal.ZERO;
    }

    public BigDecimal getIvaExento() { return ivaExento; }
    public void setIvaExento(BigDecimal ivaExento) {
        this.ivaExento = ivaExento != null ? ivaExento : BigDecimal.ZERO;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void calculateTotals() {
        this.totalIvaDebito = this.ivaVentasBienes
                .add(this.ivaVentasServicios)
                .add(this.ivaExportaciones)
                .add(this.ivaActividadesAgropecuarias);

        BigDecimal totalByPercentages = this.iva1Percent
                .add(this.iva2Percent)
                .add(this.iva4Percent)
                .add(this.iva8Percent)
                .add(this.iva10Percent)
                .add(this.iva13Percent);

        if (totalByPercentages.compareTo(this.totalIvaDebito) > 0) {
            this.totalIvaDebito = totalByPercentages;
        }

        this.totalIvaCredito = this.ivaComprasBienes
                .add(this.ivaComprasServicios)
                .add(this.ivaImportaciones)
                .add(this.ivaGastosGenerales)
                .add(this.ivaActivosFijos);

        BigDecimal diferencia = this.totalIvaDebito.subtract(this.totalIvaCredito);

        if (diferencia.compareTo(BigDecimal.ZERO) > 0) {
            this.ivaNetoPorPagar = diferencia;
            this.ivaAFavor = BigDecimal.ZERO;
        } else {
            this.ivaNetoPorPagar = BigDecimal.ZERO;
            this.ivaAFavor = diferencia.abs();
        }

        if (this.calculationDate == null) {
            this.calculationDate = LocalDate.now();
        }
    }
}