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

    // Tạo review mới (Luật mới: Chỉ cần đăng nhập)
    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody Review review) {
        
        // In ra màn hình Terminal để soi xem dữ liệu gửi từ web có bị null không
        System.out.println("DEBUG: UserID=" + review.getUserId() + 
                           ", FieldID=" + review.getFieldId() + 
                           ", Rating=" + review.getRating());

        // 1. Bắt buộc phải có User ID (Tức là phải đăng nhập rồi mới có ID gửi xuống)
        if (review.getUserId() == null) {
            return ResponseEntity.badRequest().body("Bạn cần đăng nhập để đánh giá sân!");
        }

        // 2. Chống spam: Kiểm tra xem ông này đã từng đánh giá sân này chưa
        if (reviewRepository.existsByUserIdAndFieldId(review.getUserId(), review.getFieldId())) {
            return ResponseEntity.badRequest().body("Bro đã đánh giá sân này rồi, bớt spam nha!");
        }

        // 3. Nếu qua được 2 ải trên thì cho phép lưu
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
    public ResponseEntity<Map<String, Object>> getSummary(@PathVariable Integer fieldId) {
        
        // 1. Lấy tổng số lượt đánh giá trực tiếp từ DB bằng hàm count có sẵn của nhóm
        long totalReviews = reviewRepository.countByFieldIdAndStatus(fieldId,true);
        
        // 2. Lấy điểm trung bình trực tiếp từ DB bằng câu @Query mới thêm
        Double avg = reviewRepository.getAverageRatingByFieldId(fieldId);
        if (avg == null) {
            avg = 0.0; // Nếu chưa có ai đánh giá thì mặc định là 0.0
        }

        Map<String, Object> result = new HashMap<>();
        // Làm tròn lấy 1 chữ số thập phân (Ví dụ: 4.66666... -> 4.7)
        result.put("averageRating", Math.round(avg * 10.0) / 10.0);
        result.put("totalReviews", totalReviews);
        
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