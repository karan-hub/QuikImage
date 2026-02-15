package com.image.quickimage.image.domain.Response;

public record ImageResponse(
        byte[] data,
        String contentType,
        String fileName
) {
}
