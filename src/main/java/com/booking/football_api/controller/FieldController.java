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
    @GetMapping("/search")
    public List<Field> searchFields(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String time) {
        
        LocalDateTime checkDateTime = null;

        // Nếu giao diện có gửi lên cả Ngày và Giờ thì tiến hành xử lý ghép chuỗi
        if (date != null && !date.isEmpty() && time != null && !time.isEmpty()) {
            try {
                // Ghép chuỗi lại. VD: "10/29/2026 10:30 PM"
                String dateTimeStr = date + " " + time;
                
                // Định dạng này khớp với cái ảnh giao diện của bạn (MM/dd/yyyy hh:mm a)
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a", Locale.ENGLISH);
                checkDateTime = LocalDateTime.parse(dateTimeStr, formatter);
            } catch (Exception e) {
                System.out.println("Lỗi parse ngày giờ từ Frontend: " + e.getMessage());
                // Nếu bị lỗi parse (do frontend gửi sai format), hệ thống sẽ bỏ qua lọc ngày giờ,
                // chỉ tìm theo tên và địa chỉ để không bị sập server.
            }
        }

        return fieldRepository.searchAvailableFields(name, address, checkDateTime);
    }
}   