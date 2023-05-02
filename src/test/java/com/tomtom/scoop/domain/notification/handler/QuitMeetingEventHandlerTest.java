package com.tomtom.scoop.domain.notification.handler;

import com.tomtom.scoop.domain.meeting.model.entity.Meeting;
import com.tomtom.scoop.domain.notification.event.QuitMeetingEvent;
import com.tomtom.scoop.domain.notification.model.entity.Notification;
import com.tomtom.scoop.domain.notification.model.entity.NotificationAction;
import com.tomtom.scoop.domain.notification.repository.NotificationActionRepository;
import com.tomtom.scoop.domain.notification.repository.NotificationRepository;
import com.tomtom.scoop.domain.user.model.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("QuitMeetingEventHandlerTest")
public class QuitMeetingEventHandlerTest {

    @Mock
    private NotificationActionRepository notificationActionRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private QuitMeetingEventHandler quitMeetingEventHandler;

    @Test
    @DisplayName("sendNotification 테스트")
    public void testSendNotification() {
        Meeting meeting = Meeting.builder().id(1L).title("Test Meeting").build();
        User owner = User.builder().id(2L).name("Test Owner").build();
        User quitUser = User.builder().id(3L).name("Test User").build();
        QuitMeetingEvent quitMeetingEvent = new QuitMeetingEvent(meeting, owner, quitUser);
        NotificationAction notificationAction = NotificationAction.builder().id(1L).page("meeting").contentId(1L).build();
        Notification notification = Notification.builder().id(1L).notificationAction(notificationAction)
                .title("Test Meeting - Quitting").content("Test User has quit the meeting Test Meeting")
                .isDeleted(false).isRead(false).user(owner).build();
        when(notificationActionRepository.save(any())).thenReturn(notificationAction);
        when(notificationRepository.save(any())).thenReturn(notification);

        quitMeetingEventHandler.sendNotification(quitMeetingEvent);

        verify(notificationActionRepository, times(1)).save(any());
        verify(notificationRepository, times(1)).save(any());


    }


}
