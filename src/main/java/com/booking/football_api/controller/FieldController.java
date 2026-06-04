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

import com.booking.football_api.repository.FieldImageRepository;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
            Field field = fieldRepository.findById(id).orElse(null);
            if (field == null) {
                return ResponseEntity.badRequest().body("Sân bóng không tồn tại!");
            }

            // 1. DỌN DẸP HÌNH ẢNH TRƯỚC: 
            // Lấy danh sách ảnh của sân này và xóa hết khỏi bảng FIELDIMAGE
            List<FieldImage> images = field.getImages();
            if (images != null && !images.isEmpty()) {
                fieldImageRepository.deleteAll(images);
            }

            // 2. XÓA SÂN: 
            // Lúc này sân đã sạch sẽ không còn ảnh, nếu SQL Server vẫn chặn lại
            // thì 100% là do có giao dịch đặt sân (Booking_Detail)
            fieldRepository.delete(field);
            
            return ResponseEntity.ok("Đã xóa sân thành công!");

        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // Lỗi này giờ đây chỉ có thể do bảng Booking_Detail gây ra
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
//tìm kiếm
   // API TÌM KIẾM ĐƠN GIẢN (Chỉ theo Tên và Vị trí)
    @GetMapping("/search")
    public List<Field> searchFields(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "address", required = false) String address) {
        
        // Gọi thẳng xuống Repository để tìm kiếm luôn, không cần check ngày giờ lằng nhằng nữa
        return fieldRepository.searchBasic(name, address);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> createFieldWithImage(
            @RequestParam("fieldName") String fieldName,
            @RequestParam("address") String address,
            @RequestParam("fieldTypeId") Integer fieldTypeId,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        
        try {
            // 1. Tạo Sân bóng
            Field field = new Field();
            field.setFieldName(fieldName);
            field.setAddress(address);
            field.setDescription(description);
            field.setStatus(true);
            
            Field savedField = fieldRepository.save(field);

            // 2. Xử lý lưu file ảnh vào thư mục máy tính
            if (image != null && !image.isEmpty()) {
                // Tạo thư mục 'uploads' nếu máy chưa có
                Path uploadDir = Paths.get("uploads");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                // Đặt tên file (thêm thời gian hệ thống để không bị trùng tên)
                String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                Path filePath = uploadDir.resolve(fileName);

                // Copy file ảnh vào thư mục
                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // 3. Lưu đường dẫn vào Database
                FieldImage fieldImage = new FieldImage();
                fieldImage.setField(savedField);
                // Đường dẫn này Lát nữa ta sẽ cấu hình để Frontend đọc được
                fieldImage.setImageUrl("http://localhost:8080/uploads/" + fileName);
                fieldImageRepository.save(fieldImage);
            }

            return ResponseEntity.ok(savedField);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi thêm sân: " + e.getMessage());
        }
    }
}

