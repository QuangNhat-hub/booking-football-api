package com.booking.football_api.repository;

import com.booking.football_api.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository
        extends JpaRepository<Booking, Long> {

    List<Booking> findByUserIdOrderByStartTimeDesc(Long userId);

    List<Booking> findByPitchId(Long pitchId);

    List<Booking> findByStatus(String status);

    List<Booking> findByUserIdAndStatus(Long userId, String status);

    List<Booking> findByPitchIdAndStatusNot(
            Long pitchId,
            String status
    );

}

