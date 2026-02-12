package com.image.quickimage.image.dto;

import org.springframework.web.multipart.MultipartFile;

public record FileUploadRequest(
        String name,
        String type,
        MultipartFile image
) {}