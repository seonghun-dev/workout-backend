package com.tomtom.scoop.domain.meeting.model.dto.response;

import com.tomtom.scoop.domain.meeting.model.entity.Meeting;
import com.tomtom.scoop.domain.meeting.model.entity.MeetingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class MeetingDetailResponseDto {

    @Schema(description = "Meeting id", example = "1")
    private Long id;

    @Schema(description = "Meeting title", example = "Running in the park")
    private String title;

    @Schema(description = "Meeting content", example = "Let's run together In KonKuk the park")
    private String content;

    @Schema(description = "Meeting Member count", example = "5")
    private Integer memberCount;

    @Schema(description = "Meeting Member limit", example = "10")
    private Integer memberLimit;

    @Schema(description = "Meeting Type", example = "Play")
    private String meetingType;

    @Schema(description = "Meeting exercise level", example = "Beginner")
    private String exerciseLevel;

    @Schema(description = "Meeting exercise", example = "Running")
    private String exercise;

    @Schema(description = "Meeting Owner name", example = "Seonghun")
    private String ownerName;

    @Schema(description = "Meeting Owner profile img", example = "https://scoop.s3.ap-northeast-2.amazonaws.com/42455.png")
    private String ownerProfile;

    @Schema(description = "Meeting participant profile img", example = "['https://scoop.s3.ap-northeast-2.amazonaws.com/42455.png', 'https://scoop.s3.ap-northeast-2.amazonaws.com/42455.png']")
    private List<String> meetingUserProfiles;

    @Schema(description = "User Meeting status", example = "ACCEPTED")
    private String status;

    @Schema(description = "Is user liked meeting", example = "true")
    private Boolean isLiked;

    public static MeetingDetailResponseDto fromEntity(Meeting meeting) {
        return MeetingDetailResponseDto.builder()
                .id(meeting.getId())
                .title(meeting.getTitle())
                .content(meeting.getContent())
                .memberLimit(meeting.getMemberLimit())
                .memberCount(meeting.getMemberCount())
                .ownerName(meeting.getUser().getName())
                .ownerProfile(meeting.getUser().getProfileImg())
                .meetingType(meeting.getMeetingType().getName())
                .isLiked(false)
                .meetingUserProfiles(
                        meeting.getUserMeetings().stream().filter(userMeeting -> userMeeting.getStatus() == MeetingStatus.ACCEPTED || userMeeting.getStatus() == MeetingStatus.OWNER)
                                .map(userMeeting -> userMeeting.getUser().getProfileImg()).toList())
                .exerciseLevel(meeting.getExerciseLevel().getLevel())
                .build();
    }

}
