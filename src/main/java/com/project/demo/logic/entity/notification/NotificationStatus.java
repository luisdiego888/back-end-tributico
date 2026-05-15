package com.project.demo.logic.entity.notification;

public class NotificationStatus {

    private Notification notification;
    private boolean isRead;

    public NotificationStatus(Notification notification, boolean isRead) {
        this.notification = notification;
        this.isRead = isRead;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
