package com.image.quickimage.image.api;

import com.image.quickimage.image.domain.Response.ImageResponse;
import com.image.quickimage.image.dto.FileUploadRequest;
import com.image.quickimage.image.exception.ImageNotFoundException;
import com.image.quickimage.image.exception.InvalidDimensionException;
import com.image.quickimage.image.infrastructure.StorageService;
import com.image.quickimage.image.model.ImageEntity;
import com.image.quickimage.image.repository.ImageRepository;
import com.image.quickimage.image.service.ImageProcessingService;
import com.image.quickimage.image.service.RateLimiterService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
    private RateLimiterService rateLimiterService;


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




    @GetMapping("/{name:.+}.{extension}")
    public ResponseEntity<byte[]> getImage(
            @PathVariable String name,
            @PathVariable String extension,
            @RequestParam(defaultValue = "500") Integer w,
            @RequestParam(defaultValue = "500") Integer h,
            @RequestParam(defaultValue = "80") Integer q,
            @RequestHeader(value = "Accept" , required = false) String acceptHeader
            ) throws Exception {

        if (w <= 0 || h <= 0)  throw new InvalidDimensionException("Width and height must be greater than 0");

        ImageResponse response = processingService.getProcessedImage(name, extension ,w, h, q , acceptHeader);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(response.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + response.fileName() + "\"")
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000")
                .header(HttpHeaders.VARY , HttpHeaders.ACCEPT)
                .body(response.data());
    }


    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("This is now protected automatically!");
    }


}
