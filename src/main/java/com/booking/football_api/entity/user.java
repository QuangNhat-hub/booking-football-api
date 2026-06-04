package com.booking.football_api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
@Data
@Entity
@Table(name = "[USER]") // Dùng ngoặc vuông vì USER là từ khóa của SQL Server
public class user {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "User_id")
    private Integer userId;

    @Column(name = "Role_id")
    private Integer roleId;

    @Column(name = "FullName")
    private String fullName;

    @Column(name = "Email")
    private String email;

    @Column(name = "Password")
    private String password;

    @Column(name = "Phone")
    private String phone;

    @Column(name = "Address")
    private String address;

    @Column(name = "Created_at")
    private Date createdAt;

    public Integer getUserId() {
        return userId;
    }
    @Column(name = "Status")
    private Integer status;
    // BẠN HÃY TỰ GENERATE GETTER VÀ SETTER CHO CÁC BIẾN Ở ĐÂY NHÉ
    // (Chuột phải -> Source Action -> Generate Getters and Setters)
}