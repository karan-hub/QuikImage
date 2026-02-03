package com.image.quickimage.service;

import com.image.quickimage.Request.FileUploadRequest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.function.Supplier;

@Service
public class FileUploadingService {
    public String uploadImage(FileUploadRequest request) throws IOException {

        if (request.image().isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        if (!Objects.requireNonNull(request.image().getContentType()).startsWith("image/")) {
            throw new RuntimeException("Only image files are allowed");
        }

        String uploadDir= "uploads/";
        String fileName =  System.currentTimeMillis() + "_"+request.image().getOriginalFilename();
        Path filePath = Paths.get(uploadDir,fileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, request.image().getBytes());

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
