package com.tomtom.scoop.domain.review.controller;

import com.tomtom.scoop.domain.review.model.dto.request.ReviewRequestDto;
import com.tomtom.scoop.domain.review.model.entity.Review;
import com.tomtom.scoop.domain.review.service.ReviewService;
import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.global.annotation.ReqUser;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
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
    public List<User> findAllReceivedUser(@ReqUser User user, @PathVariable("meetingId") Long meetingId) {
        return reviewService.findAllReceivedUser(user, meetingId);
    }

    @GetMapping("/received")
    @Operation(summary = "Find All User Received Reviews")
    @ResponseBody
    public List<Review> findAllReceivedReviews(@ReqUser User user) {
        return reviewService.findAllReceivedReviews(user);
    }

    @GetMapping("/reviewed")
    @Operation(summary = "Find All User Reviewed Reviews")
    @ResponseBody
    public List<Review> findAllReviewerReviews(@ReqUser User user) {
        return reviewService.findAllReviewerReviews(user);
    }

    @PostMapping
    @Operation(summary = "Create a review")
    @ResponseBody
    public Review createReview(
            @ReqUser User user,
            @RequestBody ReviewRequestDto reviewRequestDto) {
        return reviewService.createReview(user, reviewRequestDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a review")
    @ResponseBody
    public void deleteReview(
            @ReqUser User user,
            @PathVariable("id") Long id) {
        reviewService.deleteReview(user, id);
    }

}
