package com.project.demo.logic.entity.reportAdmin;

import com.project.demo.logic.entity.invoice.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportAdminInvoicesService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    private static final int MONTHS_IN_YEAR = 12;

    public Map<Integer, Integer> getMonthlyVolumeInvoices(int year) {
        Map<Integer, Integer> monthlyData = invoiceRepository.getMonthlyInvoiceVolume(year).stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).intValue(),
                        row -> ((Number) row[1]).intValue()
                ));

        return fillMissingMonths(monthlyData, 0);
    }

    public Map<String, Double> getTotalIncomeAndExpensesByYear(int year) {
        double totalIncome = getSafeDouble(invoiceRepository.getTotalIncomeByYear(year));
        double totalExpenses = getSafeDouble(invoiceRepository.getTotalExpensesByYear(year));

        return Map.of(
                "totalIncomes", totalIncome,
                "totalExpenses", totalExpenses
        );
    }

    public List<Map<String, Object>> getTop10UsersByInvoiceVolume() {
        return invoiceRepository.getTop10UsersByInvoiceVolume().stream().map(row -> Map.of(
                "id", row[0],
                "name", row[1],
                "lastname", row[2],
                "totalCountInvoices", row[3],
                "totalIncome", row[4],
                "totalExpenses", row[5]
        )).collect(Collectors.toList());
    }

    public Map<Integer, Map<String, Double>> getMonthlyIncomeAndExpenses(int year) {
        Map<Integer, Double> incomeMap = toMonthlyMap(invoiceRepository.getMonthlyIncomeByYear(year));
        Map<Integer, Double> expenseMap = toMonthlyMap(invoiceRepository.getMonthlyExpensesByYear(year));

        Map<Integer, Map<String, Double>> result = new HashMap<>();

        for (int month = 1; month <= MONTHS_IN_YEAR; month++) {
            result.put(month, Map.of(
                    "income", incomeMap.getOrDefault(month, 0.0),
                    "expenses", expenseMap.getOrDefault(month, 0.0)
            ));
        }

        return result;
    }

    public List<Map<String, Object>> getTop10UsersByBalance() {
        return invoiceRepository.getTop10UsersByBalance().stream().map(row -> Map.of(
                "id", row[0],
                "name", row[1],
                "lastname", row[2],
                "balance", row[3]
        )).collect(Collectors.toList());
    }

    private Map<Integer, Double> toMonthlyMap(List<Object[]> rows) {
        return rows.stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).intValue(),
                        row -> row[1] != null ? ((Number) row[1]).doubleValue() : 0.0
                ));
    }

    private double getSafeDouble(Double value) {
        return value != null ? value : 0.0;
    }

    private Map<Integer, Integer> fillMissingMonths(Map<Integer, Integer> originalMap, int defaultValue) {
        for (int month = 1; month <= MONTHS_IN_YEAR; month++) {
            originalMap.putIfAbsent(month, defaultValue);
        }
        return originalMap;
    }
}