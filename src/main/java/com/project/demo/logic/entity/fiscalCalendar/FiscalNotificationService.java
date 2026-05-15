package com.project.demo.logic.entity.fiscalCalendar;

import com.project.demo.logic.entity.email.EmailService;
import com.project.demo.logic.entity.notification.Notification;
import com.project.demo.logic.entity.notification.NotificationRepository;
import com.project.demo.logic.entity.notification.UserNotificationStatus;
import com.project.demo.logic.entity.notification.UserNotificationStatusRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import com.project.demo.logic.entity.weSocket.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class FiscalNotificationService {

    @Autowired
    private FiscalCalendarRepository fiscalCalendarRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserNotificationStatusRepository statusRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private WebSocketService webSocketService;

    @Scheduled(fixedRate = 30000)
    public void checkFiscalDeadlines() {
        LocalDate today = LocalDate.now();
        LocalDate threeDaysLater = today.plusDays(3);

        List<FiscalCalendar> dueEvents = fiscalCalendarRepository
                .findByTaxDeclarationDeadlineBetween(today.plusDays(1), threeDaysLater);


        for (FiscalCalendar event : dueEvents) {
            if (!notificationExistsForEvent(event)) {
                createNotificationForEvent(event);
            }
        }
    }

    private boolean notificationExistsForEvent(FiscalCalendar event) {
        return notificationRepository.existsByTypeAndCloseDate(
                "Informativa",
                event.getTaxDeclarationDeadline());
    }

    private void createNotificationForEvent(FiscalCalendar event) {
        Notification notification = new Notification();
        notification.setName("Recordatorio Fiscal: " + event.getName());

        long daysLeft = ChronoUnit.DAYS.between(
                LocalDate.now(),
                event.getTaxDeclarationDeadline());

        String message = String.format(
                "Le recordamos que el evento fiscal '%s' correspondiente al tipo '%s' tiene como fecha límite el %s. Restan %d %s para realizar la declaración.",
                event.getDescription(),
                event.getType(),
                event.getTaxDeclarationDeadline().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                daysLeft,
                daysLeft == 1 ? "día" : "días");

        notification.setDescription(message);
        notification.setType("Informativa");
        notification.setState("Activa");
        notification.setCloseDate(event.getTaxDeclarationDeadline());

        Notification savedNotification = notificationRepository.save(notification);
        sendNotificationToAllUsers(savedNotification);
    }

    private void sendNotificationToAllUsers(Notification notification) {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            UserNotificationStatus status = new UserNotificationStatus();
            status.setUser(user);
            status.setNotification(notification);
            status.setRead(false);
            statusRepository.save(status);

            emailService.sendNotificationEmail(user.getEmail(), user.getName(), notification);
        }
        webSocketService.sendGlobalNotification(notification, "CREATE");

    }

}
