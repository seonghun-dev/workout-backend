package com.tomtom.scoop.domain.notification.handler;

import com.tomtom.scoop.domain.notification.constants.NotificationPageConstants;
import com.tomtom.scoop.domain.notification.event.JoinMeetingEvent;
import com.tomtom.scoop.domain.notification.model.Notification;
import com.tomtom.scoop.domain.notification.model.NotificationAction;
import com.tomtom.scoop.domain.notification.repository.NotificationActionRepository;
import com.tomtom.scoop.domain.notification.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


/*
 * This class is responsible for sending a notification to owner when user request to join the meeting
 */
@Component
@AllArgsConstructor
public class JoinMeetingEventHandler {

    private final NotificationActionRepository NotificationActionRepository;
    private final NotificationRepository NotificationRepository;

    @EventListener
    public void sendNotification(JoinMeetingEvent event) {
        var meetingTitle = event.getMeeting().getTitle();
        var owner = event.getOwner();
        var joinedUserName = event.getJoinedUser().getName();

        NotificationAction notificationAction = NotificationAction.builder()
                .page(NotificationPageConstants.MEETINGSETTING)
                .contentId(event.getMeeting().getId())
                .build();

        Notification notification = Notification.builder()
                .user(owner)
                .title(meetingTitle + " - " + "Joining")
                .content(joinedUserName + " has requested to join the meeting " + meetingTitle)
                .isDeleted(false)
                .isRead(false)
                .notificationAction(notificationAction)
                .build();

        NotificationActionRepository.save(notificationAction);
        NotificationRepository.save(notification);

        System.out.printf("Notify : user %s has requested to join the meeting %s%n", joinedUserName, meetingTitle);
    }
}
