package com.booking.football_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.booking.football_api.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
     boolean existsByBookingId(Integer bookingId);
     
     Page<Review> findByFieldIdAndStatus(Integer fieldId, Boolean status, Pageable pa);
     
     long countByFieldIdAndStatus(Integer fieldId, Boolean status);
}






