package com.electrolytej.assembler.util;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.Notifications;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.application.ApplicationManager;

public class NotificationUtil {
    public static void showErrorNotification(String displayid, String content) {
        showNotification(displayid, content, MessageType.ERROR);
    }

    public static void showInfoNotification(String displayid, String content) {
        showNotification(displayid, content, MessageType.INFO);
    }

    public static void showNotification(String displayid, String content, MessageType type) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                NotificationGroup notificationGroup = new NotificationGroup(displayid, NotificationDisplayType.BALLOON, true);
                Project[] projects = ProjectManager.getInstance().getOpenProjects();
                Notification notification = notificationGroup.createNotification(content, type);
                Notifications.Bus.notify(notification, projects[0]);
            }
        });
    }
}
