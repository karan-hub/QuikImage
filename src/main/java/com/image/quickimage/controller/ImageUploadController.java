package com.image.quickimage.controller;

import com.image.quickimage.Request.FileUploadRequest;
import com.image.quickimage.service.FileUploadingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import java.io.IOException;


@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageUploadController {
    @Autowired
    private FileUploadingService  service;
    @PostMapping("/upload")
    public  String uploadImage(@ModelAttribute FileUploadRequest request ){
        try {
             return service.uploadImage(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("{name}")
    public ResponseEntity<Resource> getImage(@PathVariable String name ) throws Exception {
         Resource image =  service.getImage(name);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(image);
    }
}
