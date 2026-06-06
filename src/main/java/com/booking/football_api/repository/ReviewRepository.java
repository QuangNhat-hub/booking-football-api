package com.booking.football_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.booking.football_api.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
     boolean existsByBookingId(Integer bookingId);
     // Thêm dòng này vào dưới cái hàm existsByBookingId cũ của bro
     boolean existsByUserIdAndFieldId(Integer userId, Integer fieldId);
     
     Page<Review> findByFieldIdAndStatus(Integer fieldId, Boolean status, Pageable pa);
     
     long countByFieldIdAndStatus(Integer fieldId, Boolean status);

     @Query("SELECT AVG(r.rating) FROM Review r WHERE r.fieldId = :fieldId AND r.status = true")
     Double getAverageRatingByFieldId(@Param("fieldId") Integer fieldId);
}