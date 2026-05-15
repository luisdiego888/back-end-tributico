package com.project.demo.logic.entity.reportAdmin;

import com.project.demo.logic.entity.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportAdminUsersService {

    @Autowired
    private UserRepository userRepository;

    public Map<Integer, Integer> getMonthlyRegisteredUsers(int year) {
        List<Object[]> rawData = userRepository.countUsersByMonthAndYear(year);

        Map<Integer, Integer> rawDataMap = rawData.stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).intValue(),
                        row -> ((Number) row[1]).intValue()
                ));

        for (int month = 1; month <= 12; month++) {
            rawDataMap.putIfAbsent(month, 0);
        }

        return rawDataMap;
    }


    public Map<String, Integer> getProportionStatus() {
        Map<String, Integer> statusMap = new HashMap<>(Map.of(
                "active", 0,
                "blocked", 0,
                "disabled", 0
        ));

        userRepository.countUsersByStatus().forEach(row -> {
            String status = row[0].toString().toLowerCase();
            Integer total = ((Number) row[1]).intValue();
            if (statusMap.containsKey(status)) {
                statusMap.put(status, total);
            }
        });

        return statusMap;
    }
}
