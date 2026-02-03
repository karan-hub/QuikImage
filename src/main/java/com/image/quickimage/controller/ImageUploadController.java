package com.image.quickimage.controller;

import com.image.quickimage.Request.FileUploadRequest;
import com.image.quickimage.service.ImageProcessingService;
import com.image.quickimage.service.ImageUploadingService;
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
    private ImageUploadingService uploadingService;
    @Autowired
    private ImageProcessingService processingService;
    @PostMapping("/upload")
    public  String uploadImage(@ModelAttribute FileUploadRequest request ){
        try {
             return uploadingService.uploadImage(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("{name}")
    public ResponseEntity<Resource> getImage(@PathVariable String name ) throws Exception {
//         Resource image =  uploadingService.getImage(name);
        Resource image = processingService.processImage(name);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(image);
    }
}
