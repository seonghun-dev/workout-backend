package com.tomtom.scoop.domain.notification.service;

import com.tomtom.scoop.domain.notification.model.entity.Notification;
import com.tomtom.scoop.domain.notification.model.entity.NotificationAction;
import com.tomtom.scoop.domain.notification.repository.NotificationRepository;
import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.global.exception.BusinessException;
import com.tomtom.scoop.global.exception.NotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    @DisplayName("[API][Service] 알림 전체 조회 테스트")
    class FindAllNotification {

        @Nested
        @DisplayName("[API][Service] 알림 전체 조회 성공 테스트")
        class Success {


            @Test
            @DisplayName("[API][Service] 알림 전체 조회 성공 테스트1")
            void FindAllNotification1() {
                User user = User.builder().id(1L).build();
                NotificationAction notificationAction = NotificationAction.builder().id(1L)
                        .page("Meeting").contentId(1L).build();
                List<Notification> notificationList = List.of(Notification.builder().id(1L)
                        .user(user).title("hello").content("test alert1")
                        .isDeleted(false).isRead(false)
                        .notificationAction(notificationAction)
                        .build());

                ReflectionTestUtils.setField(notificationList.get(0), "createdAt",
                        LocalDateTime.of(2023, 11, 4, 1, 4));

                when(notificationRepository.findAllByUserAndIsDeletedFalse(any(User.class))).thenReturn(notificationList);

                var result = notificationService.findAllNotifications(user);

                assertThat(result).isNotNull().hasSize(1);
                var notification = result.get(0);

                assertAll(
                        () -> assertThat(notification.getCreatedAt()).isEqualTo(LocalDateTime.of(2023, 11, 4, 1, 4)),
                        () -> assertThat(notification.getId()).isEqualTo(1L),
                        () -> assertThat(notification.getTitle()).isEqualTo("hello"),
                        () -> assertThat(notification.getContent()).isEqualTo("test alert1"),
                        () -> assertThat(notification.getIsRead()).isEqualTo(false),
                        () -> assertThat(notification.getAction().getContentId()).isEqualTo(1L),
                        () -> assertThat(notification.getAction().getPage()).isEqualTo("Meeting")
                );

            }

        }


    }

    @Nested
    @DisplayName("[API][Service] 알림 읽기 완료 테스트")
    class ReadNotification {

        Notification notification;

        User user;

        @BeforeEach
        void setUp() {
            user = User.builder().id(1L).build();
            NotificationAction notificationAction = NotificationAction.builder().id(1L).page("Meeting").contentId(1L).build();
            notification = Notification.builder().id(1L).user(user).title("hello").content("test alert1").isDeleted(false).isRead(false).notificationAction(notificationAction).build();
        }

        @Nested
        @DisplayName("[API][Service] 알림 읽기 완료 성공 테스트")
        class Success {

            @Test
            @DisplayName("[API][Service] 알림 읽기 완료 성공 테스트1")
            void FindAllNotification1() {
                when(notificationRepository.findById(any())).thenReturn(Optional.of(notification));

                notificationService.markNotificationsAsRead(user, 1L);
                Assertions.assertThat(notification.getIsRead()).isTrue();
            }

        }

        @Nested
        @DisplayName("[API][Service] 알림 읽기 완료 실패 테스트")
        class Fail {

            @Test
            @DisplayName("[API][Service] 해당하는 알림 ID를 찾을 수 없는 경우")
            void FindAllNotification1() {
                when(notificationRepository.findById(any())).thenReturn(Optional.empty());

                Exception e = assertThrows(NotFoundException.class, () -> notificationService.markNotificationsAsRead(user, 1L));
                Assertions.assertThat(e.getMessage()).isEqualTo("Not Found Notification with id 1");
            }


            @Test
            @DisplayName("[API][Service] 유저의 알림이 아닌 경우")
            void FindAllNotification2() {
                when(notificationRepository.findById(any())).thenReturn(Optional.of(notification));
                User newUser = User.builder().id(2L).build();

                Exception e = assertThrows(BusinessException.class, () -> notificationService.markNotificationsAsRead(newUser, 1L));
                Assertions.assertThat(e.getMessage()).isEqualTo("Not Users Notification");

            }

        }


    }


    @Nested
    @DisplayName("[API][Service] 알림 삭제 테스트")
    class DeleteNotification {

        Notification notification;

        User user;

        @BeforeEach
        void setUp() {
            user = User.builder().id(1L).build();
            NotificationAction notificationAction = NotificationAction.builder().id(1L).page("Meeting").contentId(1L).build();
            notification = Notification.builder().id(1L).user(user).title("hello").content("test alert1").isDeleted(false).isRead(false).notificationAction(notificationAction).build();
        }

        @Nested
        @DisplayName("[API][Service] 알림 삭제 성공 테스트")
        class Success {

            @Test
            @DisplayName("[API][Service] 알림 삭제 성공 테스트1")
            void FindAllNotification1() {
                when(notificationRepository.findById(any())).thenReturn(Optional.of(notification));

                notificationService.deleteNotification(user, 1L);
                Assertions.assertThat(notification.getIsDeleted()).isTrue();
            }

        }

        @Nested
        @DisplayName("[API][Service] 알림 삭제 실패 테스트")
        class Fail {

            @Test
            @DisplayName("[API][Service] 해당하는 알림 ID를 찾을 수 없는 경우")
            void FindAllNotification1() {
                when(notificationRepository.findById(any())).thenReturn(Optional.empty());

                Exception e = assertThrows(NotFoundException.class, () -> notificationService.deleteNotification(user, 1L));
                Assertions.assertThat(e.getMessage()).isEqualTo("Not Found Notification with id 1");
            }


            @Test
            @DisplayName("[API][Service] 유저의 알림이 아닌 경우")
            void FindAllNotification2() {
                when(notificationRepository.findById(any())).thenReturn(Optional.of(notification));
                User newUser = User.builder().id(2L).build();

                Exception e = assertThrows(BusinessException.class, () -> notificationService.deleteNotification(newUser, 1L));
                Assertions.assertThat(e.getMessage()).isEqualTo("Not Users Notification");

            }

        }


    }


}
