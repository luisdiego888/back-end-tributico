package com.project.demo.rest.reportUser;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.reportUser.ReportUserService;
import com.project.demo.logic.entity.user.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("reports-user")
public class ReportUserRestController {

    @Autowired
    private ReportUserService reportUserService;

    @GetMapping("/income-and-expenses")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getAllIncomeAndExpenses(@RequestParam(defaultValue = "0") int year, @AuthenticationPrincipal User user, HttpServletRequest request) {
        Map<String, Object> incomeAndExpenses = reportUserService.getIncomeAndExpenses(year, user.getId());

        return new GlobalResponseHandler().handleResponse(
                "Reporte de ingresos y gastos recuperado exitosamente",
                incomeAndExpenses,
                HttpStatus.OK,
                request);
    }

    @GetMapping("/monthly-cash-flow")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getAllMonthlyCashFlow(@RequestParam(defaultValue = "0") int year, @AuthenticationPrincipal User user, HttpServletRequest request) {
        Map<String, Object> monthlyCashFlow = reportUserService.getMonthlyCashFlow(year, user.getId());

        return new GlobalResponseHandler().handleResponse(
                "Reporte de flujo de caja mensual recuperado exitosamente",
                monthlyCashFlow,
                HttpStatus.OK,
                request);
    }

    @GetMapping("/trimester-cash-flow")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getAllTrimesterCashFlow( @RequestParam(defaultValue = "0") int year, @AuthenticationPrincipal User user, HttpServletRequest request) {
        Map<String, Object> trimesterCashFlow = reportUserService.getTrimesterCashFlow(year, user.getId());

        return new GlobalResponseHandler().handleResponse(
                "Reporte de flujo de caja trimestral recuperado exitosamente",
                trimesterCashFlow,
                HttpStatus.OK,
                request);
    }

    @GetMapping("/top-expense-categories")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getTop5ExpenseCategories(@RequestParam(defaultValue = "0") int year, @AuthenticationPrincipal User user, HttpServletRequest request) {
        List<Map<String, Object>> top5ExpenseCategories = reportUserService.getTop5ExpenseCategories(year, user.getId());

        return new GlobalResponseHandler().handleResponse(
                "Top 5 categorías de gasto obtenidas exitosamente",
                top5ExpenseCategories,
                HttpStatus.OK,
                request);
    }
}