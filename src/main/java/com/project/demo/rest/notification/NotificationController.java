package com.project.demo.rest.notification;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.notification.*;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import com.project.demo.logic.entity.weSocket.WebSocketService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserNotificationStatusRepository statusRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebSocketService webSocketService;

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search,
            HttpServletRequest request) {

        try {

            Pageable pageable = PageRequest.of(page - 1, size);
            Page<Notification> notificationsPage;

            if (search == null || search.trim().isEmpty()) {
                notificationsPage = notificationRepository.findAll(pageable);
            } else {
                notificationsPage = notificationRepository.searchNotifications(search.trim(), pageable);
            }

            Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
            meta.setTotalPages(notificationsPage.getTotalPages());
            meta.setTotalElements(notificationsPage.getTotalElements());
            meta.setPageNumber(notificationsPage.getNumber() + 1);
            meta.setPageSize(notificationsPage.getSize());

            return new GlobalResponseHandler().handleResponse(
                    "Notificaciones recuperadas exitosamente",
                    notificationsPage.getContent(),
                    HttpStatus.OK,
                    meta
            );
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse("Error al recuperar las notificaciones: " +
                            e.getMessage(),
                    null,
                    HttpStatus.NO_CONTENT,
                    request);
        }
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getPendingNotifications(HttpServletRequest request, @AuthenticationPrincipal User user) {
        try {
            List<UserNotificationStatus> unread = statusRepository.findByUserIdAndIsReadFalseAndNotification_State(user.getId(), "Activa");

            List<Notification> notifications = unread.stream()
                    .map(UserNotificationStatus::getNotification)
                    .collect(Collectors.toList());

            return new GlobalResponseHandler().handleResponse("Notificaciones no leidas", notifications, HttpStatus.OK, request);
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse("Erro al recuerar notificaciones no leídas", null, HttpStatus.BAD_REQUEST, request);
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','USER')")
    public ResponseEntity<?> getAllNotifications(HttpServletRequest request, @AuthenticationPrincipal User user) {
        try {
            List<UserNotificationStatus> foundNotifications = statusRepository.findByUserIdAndNotification_State(user.getId(), "Activa");

            if (foundNotifications.isEmpty()) {
                return new GlobalResponseHandler().handleResponse("No hay notificaciones", null, HttpStatus.NO_CONTENT, request);
            }

            List<NotificationStatus> notifications = foundNotifications.stream()
                    .map(status -> new NotificationStatus(
                            status.getNotification(),
                            status.isRead()
                    ))
                    .collect(Collectors.toList());

            return new GlobalResponseHandler().handleResponse("Notificaciones recuperadas con exito", notifications, HttpStatus.OK, request);
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse("Ocurrio un error al  obtener las notiificaciones", null, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> create(@RequestBody Notification notification, HttpServletRequest request) {
        try {
            Notification savedNotification = notificationRepository.save(notification);

            webSocketService.sendGlobalNotification(savedNotification, "CREATE");

            List<User> users = userRepository.findAll();
            for (User user : users) {
                UserNotificationStatus status = new UserNotificationStatus();
                status.setUser(user);
                status.setNotification(savedNotification);
                status.setRead(false);
                statusRepository.save(status);
            }

            return new GlobalResponseHandler().handleResponse("Notificacion creada con exito",
                    savedNotification, HttpStatus.CREATED, request);
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse("Error al crear la notificacion: " + e.getMessage(), null,
                    HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateNotification(@PathVariable("id") Long id, @RequestBody Notification notification, HttpServletRequest request) {
        try {
            Optional<Notification> foundNotification = notificationRepository.findById(id);

            if (foundNotification.isEmpty()) {
                return new GlobalResponseHandler().handleResponse("Notificacion no encontrada",
                        null, HttpStatus.NOT_FOUND, request);
            }

            Notification updatedNotification = foundNotification.get();

            updatedNotification.setName(notification.getName());
            updatedNotification.setDescription(notification.getDescription());
            updatedNotification.setType(notification.getType());
            updatedNotification.setState(notification.getState());
            updatedNotification.setCloseDate(notification.getCloseDate());

            notificationRepository.save(updatedNotification);

            webSocketService.sendGlobalNotification(updatedNotification, "UPDATE");

            return new GlobalResponseHandler().handleResponse("Notificacion actualizada con exito"
                    , updatedNotification, HttpStatus.OK, request);

        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse("Ocurrio un error al actualizar la notificacion: " +
                            e.getMessage(),
                    null, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    @PatchMapping("/read/{notificationId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    @Transactional
    public ResponseEntity<?> markAsRead(@PathVariable Long notificationId, HttpServletRequest request, @AuthenticationPrincipal User user) {
        Optional<UserNotificationStatus> foundStatus = statusRepository.findByUserIdAndNotificationId(user.getId(), notificationId);

        if (foundStatus.isPresent()) {
            UserNotificationStatus status = foundStatus.get();
            status.setRead(true);
            statusRepository.save(status);

            webSocketService.sendGlobalNotification( status.getNotification(), "MARK_AS_READ");

            return new GlobalResponseHandler().handleResponse("Notificación marcada como leída", null, HttpStatus.OK, request);
        }
        return new GlobalResponseHandler().handleResponse("No se encontro una notificación asociada al usuario", null, HttpStatus.NOT_FOUND, request);
    }

    @PatchMapping("/read-all")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> markAllAsRead(HttpServletRequest request, @AuthenticationPrincipal User user) {
        try {
            List<UserNotificationStatus> statusList = statusRepository.findByUserIdAndNotification_State(user.getId(), "Activa");
            if (statusList.isEmpty()) {
                return new GlobalResponseHandler().handleResponse("No hay notificaciones", null, HttpStatus.NO_CONTENT, request);
            }

            for (UserNotificationStatus status : statusList) {
                status.setRead(true);
                statusRepository.save(status);
            }
            webSocketService.sendGlobalNotification( "", "MARK_ALL_AS_READ");

            return new GlobalResponseHandler().handleResponse("Notificaciones marcadas como leídas exitosamente", null, HttpStatus.OK, request);
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse("Ocurrio un error al actualizar el leido de las notificaciones", null, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    @DeleteMapping("/{notificationId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional
    public ResponseEntity<?> deleteUser(@PathVariable Long notificationId, HttpServletRequest request) {
        List<UserNotificationStatus> foundStatus = statusRepository.findByNotificationId(notificationId);
        if (!foundStatus.isEmpty()) {
            statusRepository.deleteByNotificationId(notificationId);
        }
        Optional<Notification> foundNotification = notificationRepository.findById(notificationId);
        if (foundNotification.isPresent()) {
            notificationRepository.deleteById(notificationId);
            webSocketService.sendNotificationRemoval(notificationId);

            return new GlobalResponseHandler().handleResponse("Notificacion eliminada exitosamente",
                    foundNotification.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Factura con id: " + notificationId + " no encontrado",
                    HttpStatus.NOT_FOUND, request);
        }
    }

}
