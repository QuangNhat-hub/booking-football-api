package com.booking.football_api.controller;

import com.booking.football_api.entity.Review;
import com.booking.football_api.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin("*")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    // Tạo review mới
    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody Review review) {
        if (reviewRepository.existsByBookingId(review.getBookingId())) {
            return ResponseEntity.badRequest()
                .body("Đơn đặt sân này đã được đánh giá rồi!");
        }
        review.setReviewDate(LocalDateTime.now());
        review.setStatus(true);
        return ResponseEntity.ok(reviewRepository.save(review));
    }

    // Lấy danh sách review theo sân (phân trang)
    @GetMapping("/field/{fieldId}")
    public ResponseEntity<Page<Review>> getReviews(
            @PathVariable Integer fieldId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size,
            Sort.by("reviewDate").descending());
        return ResponseEntity.ok(
            reviewRepository.findByFieldIdAndStatus(fieldId, true, pageable));
    }

    // Lấy điểm trung bình + tổng lượt
    @GetMapping("/field/{fieldId}/summary")
    public ResponseEntity<Map<String, Object>> getSummary(
            @PathVariable Integer fieldId) {
        List<Review> reviews = reviewRepository
            .findByFieldIdAndStatus(fieldId, true, Pageable.unpaged())
            .getContent();
        double avg = reviews.stream()
            .mapToInt(Review::getRating)
            .average().orElse(0.0);
        Map<String, Object> result = new HashMap<>();
        result.put("averageRating", Math.round(avg * 10.0) / 10.0);
        result.put("totalReviews", reviews.size());
        return ResponseEntity.ok(result);
    }

    // Sửa review
    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(
            @PathVariable Integer reviewId,
            @RequestParam Integer userId,
            @RequestBody Review updatedData) {
        Review review = reviewRepository.findById(reviewId).orElse(null);
        if (review == null)
            return ResponseEntity.badRequest().body("Không tìm thấy đánh giá!");
        if (!review.getUserId().equals(userId))
            return ResponseEntity.badRequest().body("Không có quyền sửa!");
        review.setRating(updatedData.getRating());
        review.setComment(updatedData.getComment());
        review.setImageUrl(updatedData.getImageUrl());
        review.setUpdatedAt(LocalDateTime.now());
        return ResponseEntity.ok(reviewRepository.save(review));
    }

    // Xóa review
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(
            @PathVariable Integer reviewId,
            @RequestParam Integer userId) {
        Review review = reviewRepository.findById(reviewId).orElse(null);
        if (review == null)
            return ResponseEntity.badRequest().body("Không tìm thấy đánh giá!");
        if (!review.getUserId().equals(userId))
            return ResponseEntity.badRequest().body("Không có quyền xóa!");
        reviewRepository.deleteById(reviewId);
        return ResponseEntity.ok("Xóa đánh giá thành công!");
    }
}






