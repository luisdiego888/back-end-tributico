package com.project.demo.logic.entity.ivaCalculation;

import com.project.demo.logic.entity.detailsInvoice.DetailsInvoice;
import com.project.demo.logic.entity.invoice.Invoice;
import com.project.demo.logic.entity.invoice.InvoiceRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class IvaCalculationService {

    @Autowired
    private IvaCalculationRepository ivaCalculationRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private UserRepository userRepository;

    public IvaCalculation createIvaSimulation(int year, int month, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        IvaCalculation ivaCalculation = new IvaCalculation(user, year, month);
        List<Invoice> invoices = invoiceRepository.findByYear(year, userId);
        processInvoicesForIva(invoices, month, ivaCalculation);
        ivaCalculation.calculateTotals();

        return ivaCalculation;
    }

    public IvaCalculation getSimulationById(Long id) {
        return ivaCalculationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Simulación no encontrada"));
    }

    public List<IvaCalculation> getUserSimulations(Long userId) {
        return ivaCalculationRepository.findByUserOrderByDateDesc(userId);
    }

    private void processInvoicesForIva(List<Invoice> invoices, int month, IvaCalculation ivaCalculation) {
        for (Invoice invoice : invoices) {
            if (invoice.getIssueDate().getMonthValue() != month) {
                continue;
            }

            if (invoice.getDetails() == null || invoice.getDetails().isEmpty()) {
                continue;
            }

            for (DetailsInvoice detail : invoice.getDetails()) {
                String category = detail.getCategory();

                if (category == null || category.trim().isEmpty()) {
                    continue;
                }

                BigDecimal ivaAmount;
                if (detail.getTaxAmount() > 0) {
                    ivaAmount = BigDecimal.valueOf(detail.getTaxAmount());
                } else if (category.equals("CBR") || category.equals("CSR")) {
                    ivaAmount = BigDecimal.valueOf(detail.getTotal())
                        .multiply(BigDecimal.valueOf(detail.getTax()).divide(BigDecimal.valueOf(100)));
                } else {
                    ivaAmount = BigDecimal.valueOf(detail.getTotal()).multiply(determineIvaRate(category));
                }

                String invoiceType = invoice.getType();

                if ("ingreso".equalsIgnoreCase(invoiceType)) {
                    processVentasDebito(category, ivaAmount, ivaCalculation);
                    BigDecimal realTaxRate = BigDecimal.valueOf(detail.getTax()).divide(BigDecimal.valueOf(100));
                    accumulateByPercentage(realTaxRate, ivaAmount, ivaCalculation);
                } else if ("gasto".equalsIgnoreCase(invoiceType)) {
                    processComprasCredito(category, ivaAmount, ivaCalculation);
                    BigDecimal realTaxRate = BigDecimal.valueOf(detail.getTax()).divide(BigDecimal.valueOf(100));
                    accumulateByPercentage(realTaxRate, ivaAmount, ivaCalculation); 
                }
            }
        }
    }

    private void processVentasDebito(String category, BigDecimal ivaAmount, IvaCalculation ivaCalculation) {
        switch (category) {
            case "VG-B" -> ivaCalculation.setIvaVentasBienes(
                    ivaCalculation.getIvaVentasBienes().add(ivaAmount));
            case "VG-S", "GSP", "GA", "GV", "PP", "MR", "HP", "SPS" ->
                    ivaCalculation.setIvaVentasServicios(
                            ivaCalculation.getIvaVentasServicios().add(ivaAmount));
            case "VE", "EXP" -> ivaCalculation.setIvaExportaciones(
                    ivaCalculation.getIvaExportaciones().add(ivaAmount));
            case "ALO" -> ivaCalculation.setIvaActividadesAgropecuarias(
                    ivaCalculation.getIvaActividadesAgropecuarias().add(ivaAmount));
            default -> ivaCalculation.setIvaVentasServicios(
                    ivaCalculation.getIvaVentasServicios().add(ivaAmount));
        }
    }

    private void processComprasCredito(String category, BigDecimal ivaAmount, IvaCalculation ivaCalculation) {
        switch (category) {
            case "CBG", "VG-B" -> ivaCalculation.setIvaComprasBienes(
                    ivaCalculation.getIvaComprasBienes().add(ivaAmount));
            case "CSG", "VG-S" -> ivaCalculation.setIvaComprasServicios(
                    ivaCalculation.getIvaComprasServicios().add(ivaAmount));
            case "IMP" -> ivaCalculation.setIvaImportaciones(
                    ivaCalculation.getIvaImportaciones().add(ivaAmount));
            case "CAF" -> ivaCalculation.setIvaActivosFijos(
                    ivaCalculation.getIvaActivosFijos().add(ivaAmount));
            case "CBR", "CSR" -> {
            }
            default -> ivaCalculation.setIvaGastosGenerales(
                    ivaCalculation.getIvaGastosGenerales().add(ivaAmount));
        }
    }

    private void accumulateByPercentage(BigDecimal ivaRate, BigDecimal ivaAmount, IvaCalculation ivaCalculation) {
        if (ivaRate.compareTo(new BigDecimal("0.01")) == 0) {
            ivaCalculation.setIva1Percent(ivaCalculation.getIva1Percent().add(ivaAmount));
        } else if (ivaRate.compareTo(new BigDecimal("0.02")) == 0) {
            ivaCalculation.setIva2Percent(ivaCalculation.getIva2Percent().add(ivaAmount));
        } else if (ivaRate.compareTo(new BigDecimal("0.04")) == 0) {
            ivaCalculation.setIva4Percent(ivaCalculation.getIva4Percent().add(ivaAmount));
        } else if (ivaRate.compareTo(new BigDecimal("0.08")) == 0) {
            ivaCalculation.setIva8Percent(ivaCalculation.getIva8Percent().add(ivaAmount));
        } else if (ivaRate.compareTo(new BigDecimal("0.10")) == 0) {
            ivaCalculation.setIva10Percent(ivaCalculation.getIva10Percent().add(ivaAmount));
        } else if (ivaRate.compareTo(new BigDecimal("0.13")) == 0) {
            ivaCalculation.setIva13Percent(ivaCalculation.getIva13Percent().add(ivaAmount));
        } else if (ivaRate.compareTo(BigDecimal.ZERO) == 0) {
            ivaCalculation.setIvaExento(ivaCalculation.getIvaExento().add(ivaAmount));
        } else {
            ivaCalculation.setIva13Percent(ivaCalculation.getIva13Percent().add(ivaAmount));
        }
    }

    private BigDecimal determineIvaRate(String category) {
        return switch (category) {
            case "CBR", "CSR" -> new BigDecimal("0.01");
            case "GSP" -> new BigDecimal("0.04");
            case "VE", "VX", "EXP", "CX", "GF", "GS", "DON", "NCE", "NCR", "MUL" -> BigDecimal.ZERO;
            case "VG-B", "VG-S", "CBG", "CSG", "GA", "SPS", "HP", "GV", "PP", "ALO", "MR", "CAF" -> new BigDecimal("0.13");
            default -> new BigDecimal("0.13");
        };
    }
}
