package com.example.lmchecker.controller;

import com.example.lmchecker.entity.Review;
import com.example.lmchecker.repository.ReviewRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewRepository reviewRepository;

    public ReviewController(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @GetMapping("/violation/{violationId}")
    public ResponseEntity<List<Review>> getReviewsByViolation(@PathVariable Long violationId) {
        return ResponseEntity.ok(reviewRepository.findByViolationIdOrderByCreatedAtDesc(violationId));
    }


    @GetMapping("/recent")
    public ResponseEntity<List<Review>> getRecentReviews() {
        return ResponseEntity.ok(reviewRepository.findAll());
    }
}
