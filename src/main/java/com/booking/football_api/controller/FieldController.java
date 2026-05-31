package com.booking.football_api.controller;

import com.booking.football_api.entity.Field;
import com.booking.football_api.repository.FieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}   