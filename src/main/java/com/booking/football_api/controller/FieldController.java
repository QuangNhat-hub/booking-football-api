package com.booking.football_api.controller;

import com.booking.football_api.entity.Field;
import com.booking.football_api.entity.FieldImage;
import com.booking.football_api.repository.FieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.dao.DataIntegrityViolationException; // Nhớ có dòng import này ở trên cùng
import com.booking.football_api.repository.FieldImageRepository;

import java.util.List;

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
// ...

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteField(@PathVariable Integer id) {
        try {
            // 1. Kiểm tra xem sân có tồn tại không
            Field field = fieldRepository.findById(id).orElse(null);
            if (field == null) {
                return ResponseEntity.badRequest().body("Sân bóng không tồn tại!");
            }

            // 2. Tiến hành xóa luôn. 
            // Nếu sân này đang có trong BOOKING_DETAIL, SQL sẽ ném lỗi ngay lập tức!
            fieldRepository.delete(field);
            return ResponseEntity.ok("Đã xóa sân thành công!");

        } catch (DataIntegrityViolationException e) {
            // 3. Hứng lỗi Khóa ngoại từ Database
            return ResponseEntity.status(422).body("Không thể xóa! Sân này đã từng có giao dịch đặt sân.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    // API Đổi trạng thái sân (Hoạt động <-> Bảo trì)
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateFieldStatus(@PathVariable Integer id, @RequestParam String status) {
        try {
            Field field = fieldRepository.findById(id).orElse(null);
            if (field == null) {
                return ResponseEntity.badRequest().body("Sân bóng không tồn tại!");
            }

            // Cập nhật trạng thái mới và lưu vào Database
            boolean boolStatus = status.equals("Đang hoạt động") ? true : false;
            field.setStatus(boolStatus);
            fieldRepository.save(field);
            
            return ResponseEntity.ok("Đã cập nhật trạng thái sân thành: " + status);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi hệ thống: " + e.getMessage());
        }
    }
    // API Cập nhật toàn bộ thông tin sân bóng
    @PutMapping("/{id}")
    public ResponseEntity<?> updateField(@PathVariable Integer id, @RequestBody Field fieldDetails) {
        try {
            // 1. Tìm sân cũ trong Database
            Field field = fieldRepository.findById(id).orElse(null);
            if (field == null) {
                return ResponseEntity.badRequest().body("Sân bóng không tồn tại!");
            }

            // 2. Ghi đè thông tin mới từ form lên sân cũ
            field.setFieldName(fieldDetails.getFieldName());
            field.setAddress(fieldDetails.getAddress());
            field.setDescription(fieldDetails.getDescription());
            // Lưu ý: Nếu entity Field của bạn có liên kết bảng Khóa Ngoại (FieldType), 
            // có thể bạn sẽ cần gọi thêm lệnh set cho các trường đó ở đây.

            // 3. Lưu lại vào DB
            Field updatedField = fieldRepository.save(field);
            return ResponseEntity.ok(updatedField);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @Autowired
    private FieldImageRepository fieldImageRepository; // Gọi thêm kho chứa ảnh

    @PostMapping
    public ResponseEntity<?> createField(@RequestBody Field field) {
        try {
            field.setStatus(true); 
            
            // 1. Cắt lấy danh sách ảnh (nếu có) do Frontend gửi lên
            List<FieldImage> images = field.getImages();
            field.setImages(null); // Tạm thời ngắt ra để lưu Sân trước
            
            // 2. Lưu Sân vào DB để lấy được ID sân
            Field savedField = fieldRepository.save(field);
            
            // 3. Nếu có ảnh, gán ID sân vừa tạo vào ảnh rồi lưu vào bảng FIELDIMAGE
            if (images != null && !images.isEmpty()) {
                for (FieldImage img : images) {
                    img.setField(savedField); // Liên kết khóa ngoại
                    fieldImageRepository.save(img);
                }
            }
            
            return ResponseEntity.ok(savedField);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi thêm sân: " + e.getMessage());
        }
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

