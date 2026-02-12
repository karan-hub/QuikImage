package com.image.quickimage.image.service;

import com.image.quickimage.image.domain.ImageNamingService;
import com.image.quickimage.image.domain.ImageProcessor;
import com.image.quickimage.image.infrastructure.CacheService;
import com.image.quickimage.image.infrastructure.StorageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service

public class ImageProcessingService {

    private ImageNamingService namingService;
    private StorageService storageService;
    private final ImageProcessor imageProcessor;
    private final CacheService cacheService;

    public ImageProcessingService(StorageService storageService,
                                  ImageNamingService namingService,
                                  ImageProcessor imageProcessor,
                                  CacheService cacheService) {
        this.storageService = storageService;
        this.namingService = namingService;
        this.imageProcessor = imageProcessor;
        this.cacheService = cacheService;
    }

    public byte[] getProcessedImage(String filename, Integer targetW, Integer targetH) throws IOException {


        String originalLocation  = "D:/quickimage/originals";
        String cacheLocation  = "D:/quickimage/cached";

        filename = filename.toLowerCase();
        Path sourcePath = storageService.getTargetPath(filename, originalLocation );


        if (!Files.exists(sourcePath)) {
            throw new FileNotFoundException("File not found");
        }

        BufferedImage original = ImageIO.read(sourcePath.toFile());

        int finalW = (targetW == null || targetW <= 0)
                ? original.getWidth()
                : targetW;

        int finalH = (targetH == null || targetH <= 0)
                ? original.getHeight()
                : targetH;


        String cacheName = namingService.ConstructName(filename, finalW, finalH);

        Path cachePath = storageService.getTargetPath(cacheName, cacheLocation );


        if (cacheService.exists(cacheName, cachePath)) {
            return cacheService.read(cachePath);
        }

        BufferedImage resized = imageProcessor.process(original, finalW, finalH);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(resized, "JPG", outputStream);

        byte[] result = outputStream.toByteArray();
        Files.write(cachePath, result);

        return result;
    }

}
