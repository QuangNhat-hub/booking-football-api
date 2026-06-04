package com.booking.football_api.repository;

import com.booking.football_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // Spring Boot sẽ tự động viết lệnh SQL kiểm tra Email đã tồn tại hay chưa
    boolean existsByEmail(String email);
}