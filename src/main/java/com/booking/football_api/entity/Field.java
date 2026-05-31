package com.booking.football_api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data 
@Entity
@Table(name = "FIELD")
public class Field {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Field_id")
    private Integer fieldId;

    @Column(name = "Fieldtype_id")
    private Integer fieldtypeId;

    @Column(name = "Field_name")
    private String fieldName;

    @Column(name = "Address")
    private String address;

    @Column(name = "Description")
    private String description;

    @Column(name = "Status")
    private Boolean status;

    @OneToMany(mappedBy = "field", fetch = FetchType.LAZY)
    private java.util.List<FieldImage> images;
}