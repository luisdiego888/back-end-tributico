package com.project.demo.logic.entity.ivaCalculation;

import com.project.demo.logic.entity.user.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Service
public class IvaExportService {

    private static final DecimalFormat decimalFormat = createDecimalFormat();

    private static DecimalFormat createDecimalFormat() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(',');
        return new DecimalFormat("#,##0.00", symbols);
    }

    private static String formatNumber(BigDecimal value) {
        return decimalFormat.format(value != null ? value : BigDecimal.ZERO);
    }

    private String row(String label, BigDecimal value) {
        return "<tr><td style='padding: 8px;'>" + label + "</td><td style='text-align: right;'>₡ " + formatNumber(value) + "</td></tr>";
    }

    public String generateSimulationPdf(IvaCalculation iva) {
        StringBuilder body = new StringBuilder();
        User user = iva.getUser();

        body.append("<div style='font-family: Arial, sans-serif;'>");

        body.append("<h1 style='font-weight: 700; font-size: 24px; border-bottom: 2px solid #3d2b1f; padding-bottom: 8px;'>")
        .append("D-104 - Declaración del Impuesto al Valor Agregado").append("</h1>");

        body.append("<div style='margin-top: 20px;'>")
        .append("<p>02 - Período: ").append(iva.getMonth()).append("/").append(iva.getYear()).append("</p>")
        .append("<p>04 - Cédula: ").append(user.getIdentification()).append("</p>")
        .append("<p>06 - Nombre: ").append(user.getName()).append(" ").append(user.getLastname()).append("</p>")
        .append("</div>");

        body.append("<h2 style='font-weight: 700; font-size: 18px; border-bottom: 2px solid black; padding-bottom: 4px; margin-bottom: 12px;'>")
        .append("I. TOTAL DE VENTAS SUJETAS, EXENTAS Y NO SUJETAS").append("</h2>")
        .append("<table style='width: 100%; border-collapse: collapse;'>")
        .append(row("Ventas de bienes", iva.getIvaVentasBienes()))
        .append(row("Ventas de servicios", iva.getIvaVentasServicios()))
        .append(row("Exportaciones", iva.getIvaExportaciones()))
        .append(row("Actividades agropecuarias", iva.getIvaActividadesAgropecuarias()))
        .append("</table>");

        body.append("<h2 style='font-weight: 700; font-size: 18px; border-bottom: 2px solid black; padding-bottom: 4px; margin-bottom: 12px;'>")
        .append("II. VENTAS POR TASA DE IVA").append("</h2>")
        .append("<table style='width: 100%; border-collapse: collapse;'>")
        .append(row("Bienes y servicios afectados al 1%", iva.getIva1Percent()))
        .append(row("Bienes y servicios afectados al 2%", iva.getIva2Percent()))
        .append(row("Bienes y servicios afectados al 4%", iva.getIva4Percent()))
        .append(row("Bienes y servicios afectados al 8%", iva.getIva8Percent()))
        .append(row("Bienes y servicios afectados al 10%", iva.getIva10Percent()))
        .append(row("Bienes y servicios afectados al 13%", iva.getIva13Percent()))
        .append(row("Ventas exentas", iva.getIvaExento()))
        .append("</table>");

        body.append("<h2 style='font-weight: 700; font-size: 18px; border-bottom: 2px solid black; padding-bottom: 4px; margin-bottom: 12px;'>")
        .append("III. TOTAL DE COMPRAS E IVA LIQUIDADO").append("</h2>")
        .append("<table style='width: 100%; border-collapse: collapse;'>")
        .append(row("IVA Compras de bienes", iva.getIvaComprasBienes()))
        .append(row("IVA Compras de servicios", iva.getIvaComprasServicios()))
        .append(row("IVA Importaciones", iva.getIvaImportaciones()))
        .append(row("IVA Gastos generales", iva.getIvaGastosGenerales()))
        .append(row("IVA Activos fijos", iva.getIvaActivosFijos()))
        .append(row("Total IVA crédito", iva.getTotalIvaCredito()))
        .append(row("Total IVA débito", iva.getTotalIvaDebito()))
        .append(row("IVA Neto a pagar", iva.getIvaNetoPorPagar()))
        .append(row("IVA a favor", iva.getIvaAFavor()))
        .append("</table>");

        return body.toString();
    }

    public String generateCsv(IvaCalculation iva) {
        StringBuilder csv = new StringBuilder();
        User user = iva.getUser();

        csv.append("D-104 - Declaración del Impuesto al Valor Agregado\n");
        csv.append("Período,").append(iva.getMonth()).append("/").append(iva.getYear()).append("\n");
        csv.append("Cédula,").append(user.getIdentification()).append("\n");
        csv.append("Nombre,").append(iva.getUser().getName()).append(" ").append(iva.getUser().getLastname()).append("\n");

        csv.append("I. TOTAL DE VENTAS SUJETAS, EXENTAS Y NO SUJETAS\n");
        csv.append("IVA Ventas Bienes,").append(formatNumber(iva.getIvaVentasBienes())).append("\n");
        csv.append("IVA Ventas Servicios,").append(formatNumber(iva.getIvaVentasServicios())).append("\n");
        csv.append("IVA Exportaciones,").append(formatNumber(iva.getIvaExportaciones())).append("\n");
        csv.append("IVA Actividades Agropecuarias,").append(formatNumber(iva.getIvaActividadesAgropecuarias())).append("\n");

        csv.append("II. VENTAS POR TASA DE IVA\n");
        csv.append("IVA 1%,").append(formatNumber(iva.getIva1Percent())).append("\n");
        csv.append("IVA 2%,").append(formatNumber(iva.getIva2Percent())).append("\n");
        csv.append("IVA 4%,").append(formatNumber(iva.getIva4Percent())).append("\n");
        csv.append("IVA 8%,").append(formatNumber(iva.getIva8Percent())).append("\n");
        csv.append("IVA 10%,").append(formatNumber(iva.getIva10Percent())).append("\n");
        csv.append("IVA 13%,").append(formatNumber(iva.getIva13Percent())).append("\n");
        csv.append("IVA Exento,").append(formatNumber(iva.getIvaExento())).append("\n");

        csv.append("III. TOTAL DE COMPRAS E IVA LIQUIDADO\n");
        csv.append("IVA Compras Bienes,").append(formatNumber(iva.getIvaComprasBienes())).append("\n");
        csv.append("IVA Compras Servicios,").append(formatNumber(iva.getIvaComprasServicios())).append("\n");
        csv.append("IVA Importaciones,").append(formatNumber(iva.getIvaImportaciones())).append("\n");
        csv.append("IVA Gastos Generales,").append(formatNumber(iva.getIvaGastosGenerales())).append("\n");
        csv.append("IVA Activos Fijos,").append(formatNumber(iva.getIvaActivosFijos())).append("\n");
        csv.append("Total IVA Débito,").append(formatNumber(iva.getTotalIvaDebito())).append("\n");
        csv.append("Total IVA Crédito,").append(formatNumber(iva.getTotalIvaCredito())).append("\n");
        csv.append("IVA Neto por Pagar,").append(formatNumber(iva.getIvaNetoPorPagar())).append("\n");
        csv.append("IVA a Favor,").append(formatNumber(iva.getIvaAFavor())).append("\n");

        return csv.toString();
    }

}


