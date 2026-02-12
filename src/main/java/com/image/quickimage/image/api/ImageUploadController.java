package com.image.quickimage.image.api;

import com.image.quickimage.image.dto.FileUploadRequest;
import com.image.quickimage.image.service.ImageProcessingService;
import com.image.quickimage.image.service.ImageUploadingService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/image")
@AllArgsConstructor
public class ImageUploadController {

    private ImageUploadingService uploadingService;

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
    public ResponseEntity<byte[]> getImage(@PathVariable String name ,  @RequestParam String w , @RequestParam String h  ) throws Exception {
         byte[] processedImage =  processingService.getProcessedImage(name, Integer.valueOf(w), Integer.valueOf(h));
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(processedImage);
    }
}
