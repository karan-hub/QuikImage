package com.image.quickimage.image.service;

import com.image.quickimage.image.dto.FileUploadRequest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;


@Service
public class ImageUploadingService {

    public String uploadImage(FileUploadRequest request) throws IOException {

        MultipartFile image = request.image();

        if (image.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        String originalName = image.getOriginalFilename();

        String extension = originalName != null && originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf("."))
                : ".jpg";

         String fileName = UUID.randomUUID() + extension;

        Path uploadDir = Paths.get("D:/quickimage/originals");
        Files.createDirectories(uploadDir);
        Path targetPath = uploadDir.resolve(fileName);

         try (InputStream in = image.getInputStream()) {
            Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        return "Image uploaded successfully: " + fileName;
    }



    public Resource getImage(String name) throws Exception {

        Path path = Paths.get("uploads").resolve(name);

        if (!Files.exists(path)) {
            throw new RuntimeException("File Not Found");
        }

        return new UrlResource(path.toUri());
    }

}
