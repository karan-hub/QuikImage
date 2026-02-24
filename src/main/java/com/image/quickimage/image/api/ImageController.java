package com.image.quickimage.image.api;

import com.image.quickimage.image.domain.Response.ImageResponse;
import com.image.quickimage.image.dto.FileUploadRequest;
import com.image.quickimage.image.dto.ManagedImageResponse;
import com.image.quickimage.image.exception.InvalidDimensionException;
import com.image.quickimage.image.infrastructure.StorageService;
import com.image.quickimage.image.model.ImageEntity;
import com.image.quickimage.image.repository.ImageRepository;
import com.image.quickimage.image.service.ImageProcessingService;
import com.image.quickimage.image.service.RateLimiterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/image")
@AllArgsConstructor
@Tag(name = "Image Delivery & AI Transformations", description = "Endpoints for intelligent image processing, smart cropping, and visual analytics.")
public class ImageController {

    private StorageService storageService;
    private ImageRepository repository;
    private ImageProcessingService processingService;
    private RateLimiterService rateLimiterService;

    @Operation(
            summary = "Upload and Index Image",
            description = "Uploads a raw image, stores it securely, and generates a base transformation URL for immediate use.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Image uploaded and indexed successfully"),
                    @ApiResponse(responseCode = "429", description = "Rate Limit Exceeded - You have consumed your token bucket quota."  ,
                            content = @Content(schema = @Schema(example = "Too many requests. Try again in 30 seconds."))),
                    @ApiResponse(responseCode = "400", description = "Invalid file or empty payload")
            }
    )
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ManagedImageResponse> upload(@ModelAttribute FileUploadRequest request) {
        MultipartFile file = request.image();

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String savedName = storageService.saveOriginal(request);
        ImageEntity entity = repository.findByName(savedName)
                .orElseThrow(() -> new RuntimeException("Upload failed to persist index"));

        return ResponseEntity.status(HttpStatus.CREATED).body(new ManagedImageResponse(
                entity.getId(),
                entity.getName(),
                entity.getContentType(),
                String.format("/api/v1/image/%s.jpg?w=500&h=500&q=80", entity.getName())));
    }


    @Operation(
            summary = "Transform & Analyze Image",
            description = "The core engine. Dynamically resizes, optimizes quality, and applies AI models for smart-cropping or color extraction. Returns raw bytes (for browsers) or JSON (for AI dashboards).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Processed image or AI metadata",
                            content = @Content(schema = @Schema(oneOf = { byte[].class, ImageResponse.class }))),
                    @ApiResponse(responseCode = "429", description = "Rate Limit Exceeded - You have consumed your token bucket quota."  ,
                            content = @Content(schema = @Schema(example = "Too many requests. Try again in 30 seconds."))),
                    @ApiResponse(responseCode = "400", description = "Invalid dimensions provided")
            }
    )
    @GetMapping("/{name:.+}.{extension}")
    public ResponseEntity<?> getImage(
            @PathVariable String name,
            @PathVariable String extension,
            @RequestParam(defaultValue = "500") Integer w,
            @RequestParam(defaultValue = "500") Integer h,
            @RequestParam(defaultValue = "80") Integer q,
            @RequestParam(required = false) String mode,
            @RequestParam(defaultValue = "false") boolean extractColors,
            @RequestHeader(value = "Accept", required = false) String acceptHeader) throws Exception {

        if (w <= 0 || h <= 0)
            throw new InvalidDimensionException("Width and height must be greater than 0");

        ImageResponse response = processingService.getProcessedImage(name, extension, w, h, q, mode, extractColors,
                acceptHeader);

        if (extractColors) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.VARY, HttpHeaders.ACCEPT)
                    .body(response);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(response.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + response.fileName() + "\"")
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000")
                .header(HttpHeaders.VARY, HttpHeaders.ACCEPT)
                .body(response.data());
    }


    @Operation(summary = "Health Check & Security Test", description = "Confirms service availability and active authentication.")
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("This is now protected automatically!");
    }

}
