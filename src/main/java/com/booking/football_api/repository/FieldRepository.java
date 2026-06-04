package com.booking.football_api.repository;

import com.booking.football_api.entity.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FieldRepository extends JpaRepository<Field, Integer> {
    
    // Tìm kiếm sân bóng theo Tên sân và Vị trí (Địa chỉ)
    @Query("SELECT f FROM Field f WHERE " +
           "(:name IS NULL OR LOWER(f.fieldName) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:address IS NULL OR LOWER(f.address) LIKE LOWER(CONCAT('%', :address, '%')))")
    List<Field> searchBasic(
            @Param("name") String name, 
            @Param("address") String address
    );
}