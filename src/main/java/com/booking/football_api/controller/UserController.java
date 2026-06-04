package com.booking.football_api.controller;

import com.booking.football_api.entity.User;
import com.booking.football_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/Users")
@CrossOrigin("*") // BẮT BUỘC CÓ: Để Frontend (chạy port khác) có thể gọi được API này
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User newUser) {
        
        // 1. Kiểm tra xem Email đã có ai đăng ký chưa
        if (userRepository.existsByEmail(newUser.getEmail())) {
            return ResponseEntity.badRequest().body("Email đã tồn tại!");
        }

        // 2. Gán ngày tạo tài khoản là ngày hiện tại
        newUser.setCreatedAt(new Date());

        // 3. Lưu xuống Database
        userRepository.save(newUser);

        return ResponseEntity.ok("Đăng ký thành công!");
    }
}