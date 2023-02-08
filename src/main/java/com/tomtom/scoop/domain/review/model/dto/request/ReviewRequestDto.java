package com.tomtom.scoop.domain.review.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewRequestDto {
    private Long meetingId;

    private Long receiverId;

    private Integer rating;

    private String comment;

}
