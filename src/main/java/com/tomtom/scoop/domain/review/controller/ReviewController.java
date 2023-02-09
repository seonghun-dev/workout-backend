package com.tomtom.scoop.domain.review.controller;

import com.tomtom.scoop.domain.review.model.dto.request.ReviewRequestDto;
import com.tomtom.scoop.domain.review.model.entity.Review;
import com.tomtom.scoop.domain.review.service.ReviewService;
import com.tomtom.scoop.domain.user.model.entity.User;
import com.tomtom.scoop.global.annotation.ReqUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/meetings/{meetingId}")
    @ResponseBody
    public List<User> findAllReceivedUser(@ReqUser User user, @PathVariable("meetingId") Long meetingId) {
        return reviewService.findAllReceivedUser(user, meetingId);
    }

    @GetMapping("/received")
    @ResponseBody
    public List<Review> findAllReceivedReviews(@ReqUser User user) {
        return reviewService.findAllReceivedReviews(user);
    }

    @GetMapping("/reviewed")
    @ResponseBody
    public List<Review> findAllReviewerReviews(@ReqUser User user) {
        return reviewService.findAllReviewerReviews(user);
    }

    @PostMapping
    @ResponseBody
    public Review createReview(
            @ReqUser User user,
            @RequestBody ReviewRequestDto reviewRequestDto) {
        return reviewService.createReview(user, reviewRequestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public void deleteReview(
            @ReqUser User user,
            @PathVariable("id") Long id) {
        reviewService.deleteReview(user, id);
    }

}
