package com.image.quickimage.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Metadata for a managed asset in the library")
public record ManagedImageResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "mumbai_skyline") String name,
        @Schema(example = "image/jpeg") String contentType,
        @Schema(example = "/api/v1/image/mumbai.jpg?w=500&h=500") String processedUrl
) {}