package com.tomtom.scoop.domain.notification.event;

import com.tomtom.scoop.domain.meeting.model.entity.Meeting;
import com.tomtom.scoop.domain.meeting.model.entity.MeetingStatus;
import com.tomtom.scoop.domain.user.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class JoinMeetingResultEvent {

    private Meeting meeting;

    private User joinedUser;

    private MeetingStatus meetingStatus;

}
