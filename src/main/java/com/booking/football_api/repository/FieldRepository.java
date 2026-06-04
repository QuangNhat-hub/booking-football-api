package com.booking.football_api.repository;

import com.booking.football_api.entity.Field;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FieldRepository extends JpaRepository<Field, Integer> {
    // Lọc theo Tên, Vị trí VÀ kiểm tra xem sân có đang trống vào ngày giờ đó không
    @Query("SELECT f FROM Field f WHERE " +
           "(:name IS NULL OR LOWER(f.fieldName) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:address IS NULL OR LOWER(f.address) LIKE LOWER(CONCAT('%', :address, '%'))) AND " +
           "(:checkDateTime IS NULL OR f.fieldId NOT IN (" +
           "   SELECT b.pitchId FROM Booking b " +
           "   WHERE b.startTime = :checkDateTime AND b.status IN ('pending', 'confirmed')" +
           "))")
    List<Field> searchAvailableFields(
            @Param("name") String name, 
            @Param("address") String address,
            @Param("checkDateTime") LocalDateTime checkDateTime
    );
}