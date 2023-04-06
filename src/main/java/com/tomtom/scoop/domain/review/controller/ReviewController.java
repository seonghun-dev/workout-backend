package com.tomtom.scoop.domain.review.controller;

import com.tomtom.scoop.domain.common.model.ResponseDto;
import com.tomtom.scoop.domain.review.model.dto.request.ReviewRequestDto;
import com.tomtom.scoop.domain.review.model.entity.Review;
import com.tomtom.scoop.domain.review.service.ReviewService;
import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.global.annotation.ReqUser;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/meetings/{meetingId}")
    @Operation(summary = "Find All Meeting Reviews User")
    @ResponseBody
    public ResponseEntity<List<User>> findAllReceivedUser(@ReqUser User user, @PathVariable("meetingId") Long meetingId) {
        var response =  reviewService.findAllReceivedUser(user, meetingId);
        return ResponseDto.ok(response);
    }

    @GetMapping("/received")
    @Operation(summary = "Find All User Received Reviews")
    @ResponseBody
    public ResponseEntity<List<Review>> findAllReceivedReviews(@ReqUser User user) {
        var response = reviewService.findAllReceivedReviews(user);
        return ResponseDto.ok(response);
    }

    @GetMapping("/reviewed")
    @Operation(summary = "Find All User Reviewed Reviews")
    @ResponseBody
    public ResponseEntity<List<Review>> findAllReviewerReviews(@ReqUser User user) {
        var response = reviewService.findAllReviewerReviews(user);
        return ResponseDto.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create a review")
    @ResponseBody
    public ResponseEntity<Review> createReview(@ReqUser User user, @RequestBody ReviewRequestDto reviewRequestDto) {
        var response = reviewService.createReview(user, reviewRequestDto);
        return ResponseDto.created(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a review")
    public ResponseEntity<Void> deleteReview(@ReqUser User user, @PathVariable("id") Long id) {
        reviewService.deleteReview(user, id);
        return ResponseDto.noContent();
    }

}
