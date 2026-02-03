package com.image.quickimage.Request;

import org.springframework.web.multipart.MultipartFile;

public record FileUploadRequest (
        String name,
        String type,
        MultipartFile image
) {
}
