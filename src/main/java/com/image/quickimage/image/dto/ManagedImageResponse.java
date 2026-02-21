package com.image.quickimage.image.dto;

public record ManagedImageResponse(
        Long id,
        String name,
        String contentType,
        String processedUrl) {
}
