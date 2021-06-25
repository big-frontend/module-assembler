package com.jamesfchen.manager;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.Notifications;
import com.intellij.openapi.ui.MessageType;

public class NotificationUtil {
    public static void showNotification(String displayid, String content) {
        NotificationGroup notificationGroup = new NotificationGroup(displayid, NotificationDisplayType.BALLOON, false);
        Notification notification = notificationGroup.createNotification(content, MessageType.INFO);
        Notifications.Bus.notify(notification);
    }
}
