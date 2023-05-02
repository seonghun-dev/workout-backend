package com.tomtom.scoop.domain.notification.handler;

import com.tomtom.scoop.domain.meeting.model.entity.Meeting;
import com.tomtom.scoop.domain.meeting.model.entity.MeetingStatus;
import com.tomtom.scoop.domain.notification.event.JoinMeetingResultEvent;
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
@DisplayName("JoinMeetingResultEventHandlerTest")
public class JoinMeetingResultEventHandlerTest {

    @Mock
    private NotificationActionRepository notificationActionRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private JoinMeetingResultEventHandler joinMeetingResultEventHandler;

    @Test
    @DisplayName("sendNotification 테스트")
    public void testSendNotification() {
        // given
        Meeting meeting = Meeting.builder().id(1L).title("Test Meeting").build();
        User owner = User.builder().id(2L).name("Test Owner").build();
        User joinedUser = User.builder().id(3L).name("Test User").build();
        JoinMeetingResultEvent joinMeetingResultEvent = new JoinMeetingResultEvent(meeting, joinedUser, MeetingStatus.ACCEPTED);
        NotificationAction notificationAction = NotificationAction.builder().id(1L).page("meeting").contentId(1L).build();
        Notification notification = Notification.builder().id(1L).notificationAction(notificationAction)
                .title("Test Meeting - Joining").content("You have joined the meeting Test Meeting with status ACCEPTED")
                .isDeleted(false).isRead(false).user(owner).build();
        when(notificationActionRepository.save(any())).thenReturn(notificationAction);
        when(notificationRepository.save(any())).thenReturn(notification);

        // when
        joinMeetingResultEventHandler.sendNotification(joinMeetingResultEvent);

        // then
        verify(notificationActionRepository, times(1)).save(any());
        verify(notificationRepository, times(1)).save(any());

    }
}
