package com.booking.football_api.repository;

import com.booking.football_api.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserIdOrderByStartTimeDesc(Long userId);
}
