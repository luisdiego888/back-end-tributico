package com.project.demo.logic.entity.fiscalCalendar;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class FiscalCalendarValidator {

    public boolean validateDeadline(LocalDate deadline) {
        if (deadline == null) {
            return false;
        }

        LocalDate minAllowedDate = LocalDate.now().plusDays(3);
        return !deadline.isBefore(minAllowedDate);
    }

}
