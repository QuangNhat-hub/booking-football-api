package com.booking.football_api.repository;

import com.booking.football_api.entity.FieldImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FieldImageRepository extends JpaRepository<FieldImage, Integer> {
    // Để trống, Spring Boot sẽ tự động viết các lệnh Thêm, Sửa, Xóa ảnh giúp bạn
}