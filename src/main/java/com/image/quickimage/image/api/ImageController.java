package com.image.quickimage.image.api;

import com.image.quickimage.image.dto.FileUploadRequest;
import com.image.quickimage.image.exception.ImageNotFoundException;
import com.image.quickimage.image.infrastructure.StorageService;
import com.image.quickimage.image.model.ImageEntity;
import com.image.quickimage.image.repository.ImageRepository;
import com.image.quickimage.image.service.ImageProcessingService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/v1/image")
@AllArgsConstructor
public class ImageController {

    private StorageService storageService;
    private ImageRepository repository;
    private ImageProcessingService processingService;


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> upload(@ModelAttribute FileUploadRequest request)   {

        MultipartFile file = request.image();

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded");
        }

        String saved = storageService.saveOriginal(request);

        return ResponseEntity.ok("Received " + saved);
    }


    @GetMapping("/all")
    public ResponseEntity<List<ImageEntity>> getAll(@ModelAttribute FileUploadRequest request)   {
        List<ImageEntity> all = repository.findAll();
        return  ResponseEntity.ok(all);
    }




    @GetMapping("{name}")
    public ResponseEntity<byte[]> getImage(@PathVariable String name ,
                                           @RequestParam(defaultValue = "500") Integer w,
                                           @RequestParam(defaultValue = "500") Integer h) throws Exception {



        byte[] imageByte =  processingService.getProcessedImage(name,  w ,h);

        MediaType contentType = storageService.getImageContentType(name);

        return ResponseEntity.ok()
                .contentType(contentType)
                .body(imageByte);
    }


}
