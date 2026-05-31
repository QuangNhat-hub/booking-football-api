package com.booking.football_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data 
@Entity
@Table(name = "FIELDIMAGE") // Khớp đúng tên bảng trong SQL
public class FieldImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Image_id")
    private Integer imageId;

    @Column(name = "Image_url")
    private String imageUrl; // Cái này chứa đường dẫn ảnh nè

    // Móc nối về bảng Field (Nhiều ảnh thuộc về 1 sân)
    @ManyToOne
    @JoinColumn(name = "Field_id")
    @JsonIgnore // Cực kỳ quan trọng: Giúp Java không bị "ngáo" vòng lặp vô tận khi trả về JSON
    private Field field;
}