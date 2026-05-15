package com.project.demo.logic.entity.isrSimulation;

import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Service
public class IsrExportService {

    private static final DecimalFormat decimalFormat = createDecimalFormat();

    private static DecimalFormat createDecimalFormat() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        return new DecimalFormat("#,##0.00", symbols);
    }

    private static String formatNumber(double number) {
        return decimalFormat.format(number);
    }

    private String row(String label, Double value) {
        return "<tr><td style='padding: 8px;'>" + label + "</td><td style='text-align: right;'>₡ " +
                formatNumber(value) + "</td></tr>";
    }

    public String generateSimulationPdf(IsrSimulation sim) {
        StringBuilder body = new StringBuilder();

        body.append("<div style='font-family: Arial, sans-serif;'>");

        body.append("<h1 style='font-weight: 700; font-size: 24px; border-bottom: 2px solid #3d2b1f; padding-bottom: 8px;'>")
        .append("D-101 - Declaración Jurada del Impuesto sobre la Renta").append("</h1>");

        body.append("<div style='margin-top: 20px;'>")
        .append("<p>02 - Período: ").append(sim.getSimulationPeriod()).append("</p>")
        .append("<p>04 - Cédula: ").append(sim.getSimulationIdentification()).append("</p>")
        .append("<p>06 - Nombre: ").append(sim.getSimulationName()).append("</p>")
        .append("</div>");

        //I. Activos y pasivos
        body.append("<h2 style='font-weight: 700; font-size: 18px; border-bottom: 2px solid black; padding-bottom: 4px; margin-bottom: 12px;'>")
        .append("I. Activos y pasivos").append("</h2>")
        .append("<table style='width: 100%; border-collapse: collapse;'>")
        .append(row("20 - Efectivo, bancos, inversiones transitorias, documentos y cuentas por cobrar", sim.getCurrentAssets()))
        .append(row("21 - Acciones y aportes en sociedades", sim.getEquityInvestments()))
        .append(row("22 - Inventarios", sim.getInventory()))
        .append(row("23 - Activos fijos (descuente la depreciacion acumulada)", sim.getNetFixedAssets()))
        .append(row("24 - Total activo neto (autocalculado)", sim.getTotalNetAssets()))
        .append(row("25 - Total pasivo", sim.getTotalLiabilities()))
        .append(row("26 - Capital neto (autocalculado)", sim.getNetEquity()))
        .append("</table>");

        // II. Ingresos y gastos
        body.append("<h2 style='font-weight: 700; font-size: 18px; border-bottom: 2px solid black; padding-bottom: 4px; margin-bottom: 12px;'>")
        .append("II. Ingresos y deducciones").append("</h2>")
        .append("<table style='width: 100%; border-collapse: collapse;'>")
        .append(row("27 - Venta de bienes y servicios, excepto los servicios profesionales", sim.getSalesRevenue()))
        .append(row("28 - Servicios profesionales y honorarios", sim.getProfessionalFees()))
        .append(row("29 - Comisiones", sim.getCommissions()))
        .append(row("30 - Intereses y rendimientos", sim.getInterestsAndYields()))
        .append(row("31 - Dividendos y participaciones", sim.getDividendsAndShares()))
        .append(row("32 - Alquileres", sim.getRents()))
        .append(row("33 - Otros ingresos diferentes a los anteriores", sim.getOtherIncome()))
        .append(row("34 - Ingresos no gravables incluidos dentro de los anteriores", sim.getNonTaxableIncome()))
        .append(row("35 - Total de renta bruta (autocalculado)", sim.getGrossIncomeTotal()))
        .append("</table>");

        // III. Costos, gastos y deducciones
        body.append("<h2 style='font-weight: 700; font-size: 18px; border-bottom: 2px solid black; padding-bottom: 4px; margin-bottom: 12px;'>")
        .append("III. Costos, gastos y deducciones").append("</h2>")
        .append("<table style='width: 100%; border-collapse: collapse;'>")
        .append(row("36 - Inventario inicial", sim.getInitialInventory()))
        .append(row("37 - Compras", sim.getPurchases()))
        .append(row("38 - Inventario final", sim.getFinalInventory()))
        .append(row("39 - Costo de ventas", sim.getCostOfGoodsSold()))
        .append(row("40 - Intereses y gastos financieros", sim.getFinancialExpenses()))
        .append(row("41 - Gastos de ventas y administrativos", sim.getAdministrativeExpenses()))
        .append(row("42 - Depreciación, amortización y agotamiento", sim.getDepreciationAndAmortization()))
        .append(row("43 - Aportes de regimenes voluntarios de pensiones complementias (Max 10% renta bruta)", sim.getPensionContributions()))
        .append(row("44 - Otros costos, gastos y deducciones permitidos por la ley", sim.getOtherAllowableDeductions()))
        .append(row("45 - Total de costos, gastos y deducciones permitidos por la ley (autocalculado)", sim.getTotalAllowableDeductions()))
        .append("</table>");

        // IV. Base imponible
        body.append("<h2 style='font-weight: 700; font-size: 18px; border-bottom: 2px solid black; padding-bottom: 4px; margin-bottom: 12px;'>")
        .append("IV. Base imponible").append("</h2>")
        .append("<table style='width: 100%; border-collapse: collapse;'>")
        .append(row("46 - Renta neta (autocalculado)", sim.getNetTaxableIncome()))
        .append(row("46 (bis) - Monto no sujeto aplicado al impuesto al salario (acumulado anual)", sim.getNonTaxableSalaryAmount()))
        .append(row("47 - Impuesto sobre la renta (autocalculado)", sim.getIncomeTax()))
        .append(row("51 - Exoneración de zona franca", sim.getFreeTradeZoneExemption()))
        .append(row("53 - Exoneración de otros conceptos", sim.getOtherExemptions()))
        .append(row("54 - Impuesto sobre la renta después de exoneraciones (autocalculado)", sim.getNetIncomeTaxAfterExemptions()))
        .append("</table>");

        // V. Créditos
        body.append("<h2 style='font-weight: 700; font-size: 18px; border-bottom: 2px solid black; padding-bottom: 4px; margin-bottom: 12px;'>")
        .append("V. Créditos").append("</h2>")
        .append("<table style='width: 100%; border-collapse: collapse;'>")
        .append(row("58 - Crédito familiar (solo personas físicas)", sim.getFamilyCredit()))
        .append(row("59 - Otros créditos", sim.getOtherCredits()))
        .append(row("60 - Impuesto del periodo (autocalculado)", sim.getPeriodTax()))
        .append(row("61 - Retenciones 2%", sim.getTwoPercentWithholdings()))
        .append(row("62 - Otras retenciones", sim.getOtherWithholdings()))
        .append(row("63 - Pagos parciales", sim.getPartialPayments()))
        .append(row("64 - Total impuesto neto (autocalculado)", sim.getTotalNetTax()))
        .append("</table>");

        // VI. Liquidación deuda tributaria
        body.append("<h2 style='font-weight: 700; font-size: 18px; border-bottom: 2px solid black; padding-bottom: 4px; margin-bottom: 12px;'>")
        .append("VI. Liquidación deuda tributaria").append("</h2>")
        .append("<table style='width: 100%; border-collapse: collapse;'>")
        .append(row("82 - Intereses (autocalculado)", sim.getInterests()))
        .append(row("83 - Total deuda tributaria (autocalculado)", sim.getTotalTaxDebt()))
        .append(row("84 - Solicito compensar con créditos a mi favor por el monto de", sim.getRequestedCompensation()))
        .append(row("85 - Total deuda por pagar (autocalculado)", sim.getTotalDebtToPay()))
        .append("</table>");

        return body.toString();
    }

    public String generateSimulationCsv(IsrSimulation sim) {
        StringBuilder csv = new StringBuilder();

        csv.append("D-101 - Declaración Jurada del Impuesto sobre la Renta\n");
        csv.append("02 - Período,").append(sim.getSimulationPeriod()).append("\n");
        csv.append("04 - Cédula,").append(sim.getSimulationIdentification()).append("\n");
        csv.append("06 - Nombre,").append(sim.getSimulationName()).append("\n");

        // I. Activos y Pasivos
        csv.append("I. Activos y pasivos\n");
        csv.append("20 - Efectivo, bancos, inversiones transitorias, documentos y cuentas por cobrar,").append(sim.getCurrentAssets()).append("\n");
        csv.append("21 - Acciones y aportes en sociedades,").append(sim.getEquityInvestments()).append("\n");
        csv.append("22 - Inventarios,").append(sim.getInventory()).append("\n");
        csv.append("23 - Activos fijos (descuente la depreciacion acumulada),").append(sim.getNetFixedAssets()).append("\n");
        csv.append("24 - Total activo neto (autocalculado),").append(sim.getTotalNetAssets()).append("\n");
        csv.append("25 - Total pasivo,").append(sim.getTotalLiabilities()).append("\n");
        csv.append("26 - Capital neto (autocalculado),").append(sim.getNetEquity()).append("\n");

        // II. Ingresos y deducciones
        csv.append("II. Ingresos y deducciones\n");
        csv.append("27 - Venta de bienes y servicios, excepto los servicios profesionales,").append(formatNumber(sim.getSalesRevenue())).append("\n");
        csv.append("28 - Servicios profesionales y honorarios,").append(formatNumber(sim.getProfessionalFees())).append("\n");
        csv.append("29 - Comisiones,").append(formatNumber(sim.getCommissions())).append("\n");
        csv.append("30 - Intereses y rendimientos,").append(formatNumber(sim.getInterestsAndYields())).append("\n");
        csv.append("31 - Dividendos y participaciones,").append(formatNumber(sim.getDividendsAndShares())).append("\n");
        csv.append("32 - Alquileres,").append(formatNumber(sim.getRents())).append("\n");
        csv.append("33 - Otros ingresos diferentes a los anteriores,").append(formatNumber(sim.getOtherIncome())).append("\n");
        csv.append("34 - Ingresos no gravables incluidos dentro de los anteriores,").append(formatNumber(sim.getNonTaxableIncome())).append("\n");
        csv.append("35 - Total de renta bruta (autocalculado),").append(formatNumber(sim.getGrossIncomeTotal())).append("\n");

        // III. Costos, gastos y deducciones
        csv.append("III. Costos, gastos y deducciones\n");
        csv.append("36 - Inventario inicial,").append(formatNumber(sim.getInitialInventory())).append("\n");
        csv.append("37 - Compras,").append(formatNumber(sim.getPurchases())).append("\n");
        csv.append("38 - Inventario final,").append(formatNumber(sim.getFinalInventory())).append("\n");
        csv.append("39 - Costo de ventas,").append(formatNumber(sim.getCostOfGoodsSold())).append("\n");
        csv.append("40 - Intereses y gastos financieros,").append(formatNumber(sim.getFinancialExpenses())).append("\n");
        csv.append("41 - Gastos de ventas y administrativos,").append(formatNumber(sim.getAdministrativeExpenses())).append("\n");
        csv.append("42 - Depreciación, amortización y agotamiento,").append(formatNumber(sim.getDepreciationAndAmortization())).append("\n");
        csv.append("43 - Aportes de regimenes voluntarios de pensiones complementias (Max 10% renta bruta),").append(formatNumber(sim.getPensionContributions())).append("\n");
        csv.append("44 - Otros costos, gastos y deducciones permitidos por la ley,").append(formatNumber(sim.getOtherAllowableDeductions())).append("\n");
        csv.append("45 - Total de costos, gastos y deducciones permitidos por la ley (autocalculado),").append(formatNumber(sim.getTotalAllowableDeductions())).append("\n");

        // IV. Base imponible
        csv.append("IV. Base imponible\n");
        csv.append("46 - Renta neta (autocalculado),").append(formatNumber(sim.getNetTaxableIncome())).append("\n");
        csv.append("46 (bis) - Monto no sujeto aplicado al impuesto al salario (acumulado anual),").append(formatNumber(sim.getNonTaxableSalaryAmount())).append("\n");
        csv.append("47 - Impuesto sobre la renta (autocalculado),").append(formatNumber(sim.getIncomeTax())).append("\n");
        csv.append("51 - Exoneración de zona franca,").append(formatNumber(sim.getFreeTradeZoneExemption())).append("\n");
        csv.append("53 - Exoneración de otros conceptos,").append(formatNumber(sim.getOtherExemptions())).append("\n");
        csv.append("54 - Impuesto sobre la renta después de exoneraciones (autocalculado),").append(formatNumber(sim.getNetIncomeTaxAfterExemptions())).append("\n");

        // V. Créditos
        csv.append("V. Créditos\n");
        csv.append("58 - Crédito familiar (solo personas físicas),").append(formatNumber(sim.getFamilyCredit())).append("\n");
        csv.append("59 - Otros créditos,").append(formatNumber(sim.getOtherCredits())).append("\n");
        csv.append("60 - Impuesto del periodo (autocalculado),").append(formatNumber(sim.getPeriodTax())).append("\n");
        csv.append("61 - Retenciones 2%,").append(formatNumber(sim.getTwoPercentWithholdings())).append("\n");
        csv.append("62 - Otras retenciones,").append(formatNumber(sim.getOtherWithholdings())).append("\n");
        csv.append("63 - Pagos parciales,").append(formatNumber(sim.getPartialPayments())).append("\n");
        csv.append("64 - Total impuesto neto (autocalculado),").append(formatNumber(sim.getTotalNetTax())).append("\n");

        // VI. Liquidación deuda tributaria
        csv.append("VI. Liquidación deuda tributaria\n");
        csv.append("82 - Intereses (autocalculado),").append(formatNumber(sim.getInterests())).append("\n");
        csv.append("83 - Total deuda tributaria (autocalculado),").append(formatNumber(sim.getTotalTaxDebt())).append("\n");
        csv.append("84 - Solicito compensar con créditos a mi favor por el monto de,").append(formatNumber(sim.getRequestedCompensation())).append("\n");
        csv.append("85 - Total deuda por pagar (autocalculado),").append(formatNumber(sim.getTotalDebtToPay())).append("\n");

        return csv.toString();
    }

}