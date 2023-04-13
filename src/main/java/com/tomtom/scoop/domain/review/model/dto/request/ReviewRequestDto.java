package com.tomtom.scoop.domain.review.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewRequestDto {
    private Long meetingId;

    private Long receiverId;

    private Integer rating;

    private String comment;

}
