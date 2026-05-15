package com.project.demo.logic.entity.isrSimulation;

import com.project.demo.logic.entity.detailsInvoice.DetailsInvoice;
import com.project.demo.logic.entity.invoice.Invoice;
import com.project.demo.logic.entity.user.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaxIsrCalculationService {

    public IsrSimulation simulate(User user, List<Invoice> invoices, int year, int childrenNumber, boolean hasSpouse) {
        IsrSimulation sim = new IsrSimulation();

        sim.setSimulationPeriod("12/" + year);
        sim.setSimulationName(user.getName().toUpperCase() + " " + user.getLastname().toUpperCase() +
                (user.getLastname2() != null && !user.getLastname2().isEmpty() ? " " + user.getLastname2().toUpperCase() : ""));
        sim.setSimulationIdentification(user.getIdentification());

        double salesRevenue = 0, professionalFees = 0, commissions = 0, interests = 0, dividends = 0, rents = 0;
        double otherIncome = 0, nonTaxableIncome = 0;
        double purchases = 0, adminExpenses = 0, financialExpenses = 0, depreciation = 0, pensionContributions = 0, otherDeductions = 0;
        double fixedAssets = 0;

        for (Invoice invoice : invoices) {
            for (DetailsInvoice detail : invoice.getDetails()) {
                String category = detail.getCategory();
                double netIncome = detail.getTotal() - detail.getTaxAmount();

                switch (category) {
                    case "VG-B" -> salesRevenue += netIncome;
                    case "VG-S" -> professionalFees += netIncome;
                    case "ALO" -> rents += netIncome;
                    case "EXP", "VE" -> otherIncome += netIncome;
                    case "VX" -> nonTaxableIncome += netIncome;
                    case "CBG", "CBR" -> purchases += netIncome;
                    case "CAF" -> fixedAssets += netIncome;
                    case "GA", "GSP", "GV", "PP", "MR", "HP", "SPS" -> adminExpenses += netIncome;
                }
            }
        }

        // I. Activos y pasivos
        sim.setCurrentAssets(0);
        sim.setEquityInvestments(0);
        sim.setInventory(0);
        sim.setNetFixedAssets(fixedAssets);
        sim.setTotalNetAssets(fixedAssets);
        sim.setTotalLiabilities(0);
        sim.setNetEquity(sim.getTotalNetAssets());

        // II. Ingresos y gastos
        sim.setSalesRevenue(salesRevenue);
        sim.setProfessionalFees(professionalFees);
        sim.setCommissions(commissions);
        sim.setInterestsAndYields(interests);
        sim.setDividendsAndShares(dividends);
        sim.setRents(rents);
        sim.setOtherIncome(otherIncome);
        sim.setNonTaxableIncome(nonTaxableIncome);

        double grossIncome = salesRevenue + professionalFees + commissions + interests + dividends + rents + otherIncome;
        sim.setGrossIncomeTotal(grossIncome);

        // III. Costos, gastos y deducciones
        double cogs = purchases;

        sim.setInitialInventory(0);
        sim.setFinalInventory(0);
        sim.setPurchases(purchases);
        sim.setCostOfGoodsSold(cogs);
        sim.setFinancialExpenses(financialExpenses);
        sim.setAdministrativeExpenses(adminExpenses);
        sim.setDepreciationAndAmortization(depreciation);
        sim.setPensionContributions(pensionContributions);
        sim.setOtherAllowableDeductions(otherDeductions);

        double totalAllowableDeductions = cogs + financialExpenses + adminExpenses + depreciation + pensionContributions + otherDeductions;
        sim.setTotalAllowableDeductions(totalAllowableDeductions);

        // IV. Base imponible
        double rentaNeta = grossIncome - totalAllowableDeductions;
        sim.setNetTaxableIncome(rentaNeta);
        sim.setNonTaxableSalaryAmount(0);

        // V. Cálculo del ISR
        double incomeTax = calculateTotalISR(rentaNeta);
        sim.setIncomeTax(incomeTax);
        sim.setFreeTradeZoneExemption(0);
        sim.setOtherExemptions(0);
        sim.setNetIncomeTaxAfterExemptions(incomeTax);

        // VI. Créditos fiscales
        double childrenCredit = 20640;
        double spouseCredit = 31200;
        double totalCredits = (childrenNumber * childrenCredit) + (hasSpouse ? spouseCredit : 0);

        sim.setFamilyCredit(totalCredits);
        sim.setOtherCredits(0);

        // Impuesto final
        double impuestoFinal = Math.max(0, Math.round(incomeTax - totalCredits));
        sim.setPeriodTax(impuestoFinal);
        sim.setTwoPercentWithholdings(0);
        sim.setOtherWithholdings(0);
        sim.setPartialPayments(0);
        sim.setTotalNetTax(impuestoFinal);

        // VII. Deuda tributaria
        sim.setInterests(0);
        sim.setTotalTaxDebt(impuestoFinal);
        sim.setRequestedCompensation(0);
        sim.setTotalDebtToPay(impuestoFinal);

        return sim;
    }

    private double calculateTotalISR(double rentaNeta) {
        final double TRAMO_1 = 4094000;
        final double TRAMO_2 = 6115000;
        final double TRAMO_3 = 10200000;
        final double TRAMO_4 = 20442000;

        double impuesto = 0;

        if (rentaNeta <= TRAMO_1) return 0;
        if (rentaNeta > TRAMO_4) {
            impuesto += (rentaNeta - TRAMO_4) * 0.25;
            rentaNeta = TRAMO_4;
        }
        if (rentaNeta > TRAMO_3) {
            impuesto += (rentaNeta - TRAMO_3) * 0.20;
            rentaNeta = TRAMO_3;
        }
        if (rentaNeta > TRAMO_2) {
            impuesto += (rentaNeta - TRAMO_2) * 0.15;
            rentaNeta = TRAMO_2;
        }
        if (rentaNeta > TRAMO_1) {
            impuesto += (rentaNeta - TRAMO_1) * 0.10;
        }
        return impuesto;
    }
}
