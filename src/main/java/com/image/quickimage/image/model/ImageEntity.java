package com.image.quickimage.image.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "images")
@Getter
@Setter
public class ImageEntity {


    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(unique = true, nullable = false)
    private  String name;
    private  String systemName;
    private  String contentType;

    private String primaryColor;   // Hex code (e.g., #f3a211)
    private String secondaryColor;

    private int focusX;
    private int focusY;
    

}
