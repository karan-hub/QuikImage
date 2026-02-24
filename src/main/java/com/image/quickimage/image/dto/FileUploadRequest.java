package com.image.quickimage.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "Request object for uploading new assets")
public record FileUploadRequest(
        @Schema(example = "sunset_mumbai", description = "Custom name for the image") String name,
        @Schema(example = "png", description = "Target file extension") String type,
        @Schema(description = "The binary image file") MultipartFile image
) {}