package com.tomtom.scoop.domain.notification.handler;

import com.tomtom.scoop.domain.notification.constants.NotificationPageConstants;
import com.tomtom.scoop.domain.notification.event.JoinMeetingResultEvent;
import com.tomtom.scoop.domain.notification.model.Notification;
import com.tomtom.scoop.domain.notification.model.NotificationAction;
import com.tomtom.scoop.domain.notification.repository.NotificationActionRepository;
import com.tomtom.scoop.domain.notification.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/*
 * This class is responsible for sending a notification to the user when owner accept or reject the request to join the meeting
 */
@Component
@AllArgsConstructor
public class JoinMeetingResultEventHandler {

    private final NotificationActionRepository NotificationActionRepository;
    private final NotificationRepository NotificationRepository;

    @EventListener
    public void sendNotification(JoinMeetingResultEvent event) {
        var meetingTitle = event.getMeeting().getTitle();
        var meetingStatus = event.getMeetingStatus();
        var user = event.getJoinedUser();

        NotificationAction notificationAction = NotificationAction.builder()
                .page(NotificationPageConstants.MEETING)
                .contentId(event.getMeeting().getId())
                .build();

        Notification notification = Notification.builder()
                .user(event.getJoinedUser())
                .title(meetingTitle + " - " + meetingStatus)
                .content("You have joined the meeting " + meetingTitle + " with status " + meetingStatus)
                .isDeleted(false)
                .isRead(false)
                .notificationAction(notificationAction)
                .build();

        NotificationActionRepository.save(notificationAction);
        NotificationRepository.save(notification);

        System.out.printf("Notify : user %s :  meeting %s - status %s%n", user, meetingTitle, meetingStatus);
    }
}
