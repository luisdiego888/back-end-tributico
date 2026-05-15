package com.project.demo.logic.entity.isrSimulation;

import com.project.demo.logic.entity.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Table(name = "isr_simulation")
public class IsrSimulation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // I. Datos generales
    private String simulationPeriod;
    private String simulationName;
    private String simulationIdentification;

    // I. Activos y pasivos
    private double currentAssets;
    private double equityInvestments;
    private double inventory;
    private double netFixedAssets;
    private double totalNetAssets;
    private double totalLiabilities;
    private double netEquity;

    // II. Ingresos y gastos
    private double salesRevenue;
    private double professionalFees;
    private double commissions;
    private double interestsAndYields;
    private double dividendsAndShares;
    private double rents;
    private double otherIncome;
    private double nonTaxableIncome;
    private double grossIncomeTotal;

    // III. Costos, gastos y deducciones
    private double initialInventory;
    private double purchases;
    private double finalInventory;
    private double costOfGoodsSold;
    private double financialExpenses;
    private double administrativeExpenses;
    private double depreciationAndAmortization;
    private double pensionContributions;
    private double otherAllowableDeductions;
    private double totalAllowableDeductions;

    // IV. Base imponible
    private double netTaxableIncome;
    private double nonTaxableSalaryAmount;
    private double incomeTax;
    private double freeTradeZoneExemption;
    private double otherExemptions;
    private double netIncomeTaxAfterExemptions;

    // V. Creditos
    private double familyCredit;
    private double otherCredits;
    private double periodTax;
    private double twoPercentWithholdings;
    private double otherWithholdings;
    private double partialPayments;
    private double totalNetTax;

    // VI. Liquidacion deuda tributaria
    private double interests;
    private double totalTaxDebt;
    private double requestedCompensation;
    private double totalDebtToPay;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSimulationPeriod() {
        return simulationPeriod;
    }

    public void setSimulationPeriod(String simulationPeriod) {
        this.simulationPeriod = simulationPeriod;
    }

    public String getSimulationName() {
        return simulationName;
    }

    public void setSimulationName(String simulationName) {
        this.simulationName = simulationName;
    }

    public String getSimulationIdentification() {
        return simulationIdentification;
    }

    public void setSimulationIdentification(String simulationIdentification) {
        this.simulationIdentification = simulationIdentification;
    }

    public double getCurrentAssets() {
        return currentAssets;
    }

    public void setCurrentAssets(double currentAssets) {
        this.currentAssets = currentAssets;
    }

    public double getEquityInvestments() {
        return equityInvestments;
    }

    public void setEquityInvestments(double equityInvestments) {
        this.equityInvestments = equityInvestments;
    }

    public double getInventory() {
        return inventory;
    }

    public void setInventory(double inventory) {
        this.inventory = inventory;
    }

    public double getNetFixedAssets() {
        return netFixedAssets;
    }

    public void setNetFixedAssets(double netFixedAssets) {
        this.netFixedAssets = netFixedAssets;
    }

    public double getTotalNetAssets() {
        return totalNetAssets;
    }

    public void setTotalNetAssets(double totalNetAssets) {
        this.totalNetAssets = totalNetAssets;
    }

    public double getTotalLiabilities() {
        return totalLiabilities;
    }

    public void setTotalLiabilities(double totalLiabilities) {
        this.totalLiabilities = totalLiabilities;
    }

    public double getNetEquity() {
        return netEquity;
    }

    public void setNetEquity(double netEquity) {
        this.netEquity = netEquity;
    }

    public double getSalesRevenue() {
        return salesRevenue;
    }

    public void setSalesRevenue(double salesRevenue) {
        this.salesRevenue = salesRevenue;
    }

    public double getProfessionalFees() {
        return professionalFees;
    }

    public void setProfessionalFees(double professionalFees) {
        this.professionalFees = professionalFees;
    }

    public double getCommissions() {
        return commissions;
    }

    public void setCommissions(double commissions) {
        this.commissions = commissions;
    }

    public double getInterestsAndYields() {
        return interestsAndYields;
    }

    public void setInterestsAndYields(double interestsAndYields) {
        this.interestsAndYields = interestsAndYields;
    }

    public double getDividendsAndShares() {
        return dividendsAndShares;
    }

    public void setDividendsAndShares(double dividendsAndShares) {
        this.dividendsAndShares = dividendsAndShares;
    }

    public double getRents() {
        return rents;
    }

    public void setRents(double rents) {
        this.rents = rents;
    }

    public double getOtherIncome() {
        return otherIncome;
    }

    public void setOtherIncome(double otherIncome) {
        this.otherIncome = otherIncome;
    }

    public double getNonTaxableIncome() {
        return nonTaxableIncome;
    }

    public void setNonTaxableIncome(double nonTaxableIncome) {
        this.nonTaxableIncome = nonTaxableIncome;
    }

    public double getGrossIncomeTotal() {
        return grossIncomeTotal;
    }

    public void setGrossIncomeTotal(double grossIncomeTotal) {
        this.grossIncomeTotal = grossIncomeTotal;
    }

    public double getInitialInventory() {
        return initialInventory;
    }

    public void setInitialInventory(double initialInventory) {
        this.initialInventory = initialInventory;
    }

    public double getPurchases() {
        return purchases;
    }

    public void setPurchases(double purchases) {
        this.purchases = purchases;
    }

    public double getFinalInventory() {
        return finalInventory;
    }

    public void setFinalInventory(double finalInventory) {
        this.finalInventory = finalInventory;
    }

    public double getCostOfGoodsSold() {
        return costOfGoodsSold;
    }

    public void setCostOfGoodsSold(double costOfGoodsSold) {
        this.costOfGoodsSold = costOfGoodsSold;
    }

    public double getFinancialExpenses() {
        return financialExpenses;
    }

    public void setFinancialExpenses(double financialExpenses) {
        this.financialExpenses = financialExpenses;
    }

    public double getAdministrativeExpenses() {
        return administrativeExpenses;
    }

    public void setAdministrativeExpenses(double administrativeExpenses) {
        this.administrativeExpenses = administrativeExpenses;
    }

    public double getDepreciationAndAmortization() {
        return depreciationAndAmortization;
    }

    public void setDepreciationAndAmortization(double depreciationAndAmortization) {
        this.depreciationAndAmortization = depreciationAndAmortization;
    }

    public double getPensionContributions() {
        return pensionContributions;
    }

    public void setPensionContributions(double pensionContributions) {
        this.pensionContributions = pensionContributions;
    }

    public double getOtherAllowableDeductions() {
        return otherAllowableDeductions;
    }

    public void setOtherAllowableDeductions(double otherAllowableDeductions) {
        this.otherAllowableDeductions = otherAllowableDeductions;
    }

    public double getTotalAllowableDeductions() {
        return totalAllowableDeductions;
    }

    public void setTotalAllowableDeductions(double totalAllowableDeductions) {
        this.totalAllowableDeductions = totalAllowableDeductions;
    }

    public double getNetTaxableIncome() {
        return netTaxableIncome;
    }

    public void setNetTaxableIncome(double netTaxableIncome) {
        this.netTaxableIncome = netTaxableIncome;
    }

    public double getNonTaxableSalaryAmount() {
        return nonTaxableSalaryAmount;
    }

    public void setNonTaxableSalaryAmount(double nonTaxableSalaryAmount) {
        this.nonTaxableSalaryAmount = nonTaxableSalaryAmount;
    }

    public double getIncomeTax() {
        return incomeTax;
    }

    public void setIncomeTax(double incomeTax) {
        this.incomeTax = incomeTax;
    }

    public double getFreeTradeZoneExemption() {
        return freeTradeZoneExemption;
    }

    public void setFreeTradeZoneExemption(double freeTradeZoneExemption) {
        this.freeTradeZoneExemption = freeTradeZoneExemption;
    }

    public double getOtherExemptions() {
        return otherExemptions;
    }

    public void setOtherExemptions(double otherExemptions) {
        this.otherExemptions = otherExemptions;
    }

    public double getNetIncomeTaxAfterExemptions() {
        return netIncomeTaxAfterExemptions;
    }

    public void setNetIncomeTaxAfterExemptions(double netIncomeTaxAfterExemptions) {
        this.netIncomeTaxAfterExemptions = netIncomeTaxAfterExemptions;
    }

    public double getFamilyCredit() {
        return familyCredit;
    }

    public void setFamilyCredit(double familyCredit) {
        this.familyCredit = familyCredit;
    }

    public double getOtherCredits() {
        return otherCredits;
    }

    public void setOtherCredits(double otherCredits) {
        this.otherCredits = otherCredits;
    }

    public double getPeriodTax() {
        return periodTax;
    }

    public void setPeriodTax(double periodTax) {
        this.periodTax = periodTax;
    }

    public double getTwoPercentWithholdings() {
        return twoPercentWithholdings;
    }

    public void setTwoPercentWithholdings(double twoPercentWithholdings) {
        this.twoPercentWithholdings = twoPercentWithholdings;
    }

    public double getOtherWithholdings() {
        return otherWithholdings;
    }

    public void setOtherWithholdings(double otherWithholdings) {
        this.otherWithholdings = otherWithholdings;
    }

    public double getPartialPayments() {
        return partialPayments;
    }

    public void setPartialPayments(double partialPayments) {
        this.partialPayments = partialPayments;
    }

    public double getTotalNetTax() {
        return totalNetTax;
    }

    public void setTotalNetTax(double totalNetTax) {
        this.totalNetTax = totalNetTax;
    }

    public double getInterests() {
        return interests;
    }

    public void setInterests(double interests) {
        this.interests = interests;
    }

    public double getTotalTaxDebt() {
        return totalTaxDebt;
    }

    public void setTotalTaxDebt(double totalTaxDebt) {
        this.totalTaxDebt = totalTaxDebt;
    }

    public double getRequestedCompensation() {
        return requestedCompensation;
    }

    public void setRequestedCompensation(double requestedCompensation) {
        this.requestedCompensation = requestedCompensation;
    }

    public double getTotalDebtToPay() {
        return totalDebtToPay;
    }

    public void setTotalDebtToPay(double totalDebtToPay) {
        this.totalDebtToPay = totalDebtToPay;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
}
