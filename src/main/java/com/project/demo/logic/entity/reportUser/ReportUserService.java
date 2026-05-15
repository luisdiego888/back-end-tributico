package com.project.demo.logic.entity.reportUser;

import com.project.demo.logic.entity.invoice.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportUserService {

    @Autowired
    InvoiceRepository invoiceRepository;

    private static final int MONTHS_IN_YEAR = 12;
    private static final int TRIMESTER_IN_YEAR = 4;

    public Map<String, Object> getIncomeAndExpenses(int year, Long userId) {
        Map<String, Double> incomes = getMonthlyTotals(year, "ingreso", userId);
        Map<String, Double> expenses = getMonthlyTotals(year, "gasto", userId);

        return Map.of(
                "income", incomes,
                "expenses", expenses
        );
    }

    public Map<String, Object> getMonthlyCashFlow(int year, Long userId) {
        Map<String, Double> incomes = getMonthlyTotals(year, "ingreso", userId);
        Map<String, Double> expenses = getMonthlyTotals(year, "gasto", userId);
        Map<String, Double> cashFlow = getCashFlow(incomes, expenses);

        return Map.of(
                "income", incomes,
                "expenses", expenses,
                "cashFlow", cashFlow
        );
    }

    public Map<String, Object> getTrimesterCashFlow(int year, Long userId) {
        Map<String, Double> incomesMonthly = getMonthlyTotals(year, "ingreso", userId);
        Map<String, Double> expensesMonthly = getMonthlyTotals(year, "gasto", userId);

        Map<String, Double> incomeTrimester = getTrimesterTotals(incomesMonthly);
        Map<String, Double> expensesTrimester = getTrimesterTotals(expensesMonthly);
        Map<String, Double> cashFlow = getCashFlow(incomeTrimester, expensesTrimester);

        return Map.of(
                "income", incomeTrimester,
                "expenses", expensesTrimester,
                "cashFlow", cashFlow
        );
    }

    public List<Map<String, Object>> getTop5ExpenseCategories(int year, Long userId) {
        List<Object[]> results = invoiceRepository.getTop5ExpenseCategoriesByYear(year, userId);

        return results.stream()
                .map(row -> Map.of(
                        "category", row[0],
                        "total", row[1]
                ))
                .collect(Collectors.toList());
    }

    public Map<String, Double> getMonthlyTotals(int year, String type, Long userId) {
        List<Object[]> rawData = invoiceRepository.getMonthlyInvoiceTotals(year, type, userId);

        Map<Integer, Double> totalsMap = rawData.stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).intValue(),
                        row -> ((Number) row[1]).doubleValue()
                ));

        Map<String, Double> result = new LinkedHashMap<>();
        for (int month = 1; month <= MONTHS_IN_YEAR; month++) {
            result.put(String.valueOf(month), totalsMap.getOrDefault(month, 0.0));
        }

        return result;
    }

    public Map<String, Double> getTrimesterTotals(Map<String, Double> monthlyTotals) {
        Map<String, Double> result = new LinkedHashMap<>();

        for (int i = 0; i < TRIMESTER_IN_YEAR; i++) {
            double sum = 0.0;
            for (int j = i * 3 + 1; j <= (i + 1) * 3; j++) {
                sum += monthlyTotals.getOrDefault(String.valueOf(j), 0.0);
            }
            result.put(String.valueOf(i + 1), sum);
        }

        return result;
    }

    public Map<String, Double> getCashFlow(Map<String, Double> income, Map<String, Double> expenses) {
        Map<String, Double> result = new LinkedHashMap<>();

        for (int i = 1; i <= income.size(); i++) {
            String key = String.valueOf(i);
            double inflow = income.getOrDefault(key, 0.0);
            double outflow = expenses.getOrDefault(key, 0.0);
            result.put(key, inflow - outflow);
        }

        return result;
    }
}