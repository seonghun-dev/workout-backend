package com.tomtom.scoop.domain.notification.event;

import com.tomtom.scoop.domain.meeting.model.entity.Meeting;
import com.tomtom.scoop.domain.user.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class JoinMeetingEvent {
    private Meeting meeting;
    private User owner;
    private User joinedUser;
}
