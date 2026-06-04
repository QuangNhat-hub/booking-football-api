package com.booking.football_api.controller;

import com.booking.football_api.entity.Field;
import com.booking.football_api.repository.FieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/fields")
@CrossOrigin("*") // Cực kỳ quan trọng: Mở cửa cho trang HTML của bro gọi vào không bị chặn
public class FieldController {

    @Autowired
    private FieldRepository fieldRepository;

    @GetMapping
    public List<Field> getAllFields() {
        return fieldRepository.findAll();
    }
    @GetMapping("/{id}")
    public Field getFieldById(@PathVariable Integer id) {
        return fieldRepository.findById(id).orElse(null);
    }
    // API HỖ TRỢ LỌC TÊN, ĐỊA CHỈ, NGÀY VÀ GIỜ
   // Thay thế API /search cũ bằng đoạn này
    @GetMapping("/search")
    public List<Field> searchFields(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "time", required = false) String time) {
        
        // Nếu giao diện CÓ gửi cả ngày và giờ
        if (date != null && !date.isEmpty() && time != null && !time.isEmpty()) {
            try {
                String dateTimeStr = date + "T" + time + ":00";
                java.time.LocalDateTime checkDateTime = java.time.LocalDateTime.parse(dateTimeStr);
                
                // Trả về kết quả tìm kiếm có check lịch trống
                return fieldRepository.searchWithTime(name, address, checkDateTime);
            } catch (Exception e) {
                System.out.println("Lỗi ngày giờ: " + e.getMessage());
            }
        }

        // Nếu KHÔNG nhập ngày giờ (hoặc bị lỗi) thì chỉ tìm theo Tên và Địa chỉ
        return fieldRepository.searchBasic(name, address);
    }
}   