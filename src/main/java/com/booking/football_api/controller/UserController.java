package com.booking.football_api.controller;

import com.booking.football_api.entity.User;
import com.booking.football_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/users")
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
        newUser.setStatus(1);
        // 3. Lưu xuống Database
        userRepository.save(newUser);

        return ResponseEntity.ok("Đăng ký thành công!");
    }
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User loginRequest) {
        // 1. Tìm xem số điện thoại có tồn tại trong database không
        User user = userRepository.findByPhone(loginRequest.getPhone());
        
        if (user == null) {
            return ResponseEntity.badRequest().body("Số điện thoại không tồn tại trên hệ thống!");
        }

        // 2. Kiểm tra mật khẩu
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.badRequest().body("Mật khẩu không chính xác!");
        }

        // 3. Nếu đúng hết, trả về thông tin user
        return ResponseEntity.ok(user);
    }
}
