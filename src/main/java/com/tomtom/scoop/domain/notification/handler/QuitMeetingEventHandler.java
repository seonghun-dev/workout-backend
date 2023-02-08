package com.tomtom.scoop.domain.notification.handler;

import com.tomtom.scoop.domain.notification.constants.NotificationPageConstants;
import com.tomtom.scoop.domain.notification.event.QuitMeetingEvent;
import com.tomtom.scoop.domain.notification.model.entity.Notification;
import com.tomtom.scoop.domain.notification.model.entity.NotificationAction;
import com.tomtom.scoop.domain.notification.repository.NotificationActionRepository;
import com.tomtom.scoop.domain.notification.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class QuitMeetingEventHandler {

    private final NotificationActionRepository NotificationActionRepository;
    private final NotificationRepository NotificationRepository;

    @EventListener
    public void sendNotification(QuitMeetingEvent event) {
        var meetingTitle = event.getMeeting().getTitle();
        var joinedUserName = event.getQuitUser().getName();

        NotificationAction notificationAction = NotificationAction.builder()
                .page(NotificationPageConstants.MEETINGSETTING)
                .contentId(event.getMeeting().getId())
                .build();

        Notification notification = Notification.builder()
                .user(event.getOwner())
                .title(meetingTitle + " - " + "Joining")
                .content(joinedUserName + " has requested to quit the meeting " + meetingTitle)
                .isDeleted(false)
                .isRead(false)
                .notificationAction(notificationAction)
                .build();

        NotificationActionRepository.save(notificationAction);
        NotificationRepository.save(notification);

        System.out.printf("Notify : user %s has requested to quit the meeting %s%n", joinedUserName, meetingTitle);
    }
}
