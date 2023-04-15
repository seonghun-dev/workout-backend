package com.tomtom.scoop.domain.notification.service;

import com.tomtom.scoop.domain.common.BaseTimeEntity;
import com.tomtom.scoop.domain.notification.model.entity.Notification;
import com.tomtom.scoop.domain.notification.model.entity.NotificationAction;
import com.tomtom.scoop.domain.notification.repository.NotificationRepository;
import com.tomtom.scoop.domain.user.model.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("[API][Service] 알림 관련 테스트")
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;


    @Nested
    @DisplayName("[API][Service] 모임 전체 조회 테스트")
    class FindAllNotification {

        @Nested
        @DisplayName("[API][Service] 모임 전체 조회 성공 테스트")
        class Success {

            @Test
            @DisplayName("[API][Service] 모임 전체 조회 성공 테스트1")
            void FindAllNotification1() {
                User user = User.builder().id(1L).build();
                NotificationAction notificationAction = NotificationAction.builder().id(1L).page("Meeting").contentId(1L).build();
                List<Notification> notificationList = List.of(Notification.builder().id(1L).user(user).title("hello").content("test alert1").isDeleted(false).isRead(false).notificationAction(notificationAction).build());
                ReflectionTestUtils.setField(
                        notificationList.get(0),
                        BaseTimeEntity.class,
                        "createdAt",
                        LocalDateTime.of(2023, 11, 4, 1, 4),
                        LocalDateTime.class
                );
                when(notificationRepository.findAllByUserAndIsDeletedFalse(any(User.class))).thenReturn(notificationList);

                var result = notificationService.findAllNotifications(user);

                assertAll(
                        () -> Assertions.assertThat(result).isNotNull(),
                        () -> Assertions.assertThat(result).hasSize(1),
                        () -> Assertions.assertThat(result.get(0).getCreatedAt()).isEqualTo(LocalDateTime.of(2023, 11, 4, 1, 4)),
                        () -> Assertions.assertThat(result.get(0).getId()).isEqualTo(1L),
                        () -> Assertions.assertThat(result.get(0).getTitle()).isEqualTo("hello"),
                        () -> Assertions.assertThat(result.get(0).getContent()).isEqualTo("test alert1"),
                        () -> Assertions.assertThat(result.get(0).getIsRead()).isEqualTo(false),
                        () -> Assertions.assertThat(result.get(0).getAction().getContentId()).isEqualTo(1L),
                        () -> Assertions.assertThat(result.get(0).getAction().getPage()).isEqualTo("Meeting")
                );

            }

        }


    }


}
