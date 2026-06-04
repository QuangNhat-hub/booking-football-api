package com.booking.football_api.repository;

import com.booking.football_api.entity.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FieldRepository extends JpaRepository<Field, Integer> {
    
    // 1. TÌM CƠ BẢN (Khi ô Ngày/Giờ bị bỏ trống)
    @Query("SELECT f FROM Field f WHERE " +
           "(:name IS NULL OR LOWER(f.fieldName) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:address IS NULL OR LOWER(f.address) LIKE LOWER(CONCAT('%', :address, '%')))")
    List<Field> searchBasic(
            @Param("name") String name, 
            @Param("address") String address
    );

    // 2. TÌM NÂNG CAO (Khi có chọn Ngày/Giờ)
    @Query("SELECT f FROM Field f WHERE " +
           "(:name IS NULL OR LOWER(f.fieldName) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:address IS NULL OR LOWER(f.address) LIKE LOWER(CONCAT('%', :address, '%'))) AND " +
           "f.fieldId NOT IN (" +
           "   SELECT CAST(b.pitchId AS int) FROM Booking b " +
           "   WHERE b.startTime = :checkDateTime AND b.status IN ('pending', 'confirmed')" +
           ")")
    List<Field> searchWithTime(
            @Param("name") String name, 
            @Param("address") String address,
            @Param("checkDateTime") LocalDateTime checkDateTime
    );
}