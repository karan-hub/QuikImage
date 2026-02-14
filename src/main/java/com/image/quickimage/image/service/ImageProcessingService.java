package com.image.quickimage.image.service;

import com.image.quickimage.image.domain.ImageNamingService;
import com.image.quickimage.image.domain.ImageProcessor;
import com.image.quickimage.image.domain.SafeDimension;
import com.image.quickimage.image.config.StorageProperties;
import com.image.quickimage.image.exception.ImageNotFoundException;
import com.image.quickimage.image.infrastructure.CacheService;
import com.image.quickimage.image.infrastructure.StorageService;
import com.image.quickimage.image.model.ImageEntity;
import com.image.quickimage.image.repository.ImageRepository;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@ConfigurationProperties(prefix = "app.storage")
public class ImageProcessingService {

    private final ImageNamingService namingService;
    private final StorageService storageService;
    private final ImageProcessor imageProcessor;
    private final CacheService cacheService;
    private  final  ValidationService validationService;
    private final StorageProperties storageProperties;
    private final ImageRepository imageRepository;


    public ImageProcessingService(StorageService storageService,
                                  ImageNamingService namingService,
                                  ImageProcessor imageProcessor,
                                  CacheService cacheService,
                                  ValidationService validationService,
                                  StorageProperties storageProperties, ImageRepository imageRepository) {
        this.storageService = storageService;
        this.namingService = namingService;
        this.imageProcessor = imageProcessor;
        this.cacheService = cacheService;
        this.validationService = validationService;
        this.storageProperties = storageProperties;
        this.imageRepository = imageRepository;
    }

    public byte[] getProcessedImage(String filename, Integer requestedW, Integer requestedH) throws  Exception {

        String sanitizedName = filename.replaceAll("[^a-zA-Z0-9-]", "_");
        ImageEntity imageInfo = imageRepository.findByName(sanitizedName)
                .orElseThrow(() -> new ImageNotFoundException("Image not found in DB :-" + filename));

        String imageSystemName = imageInfo.getSystemName();
        String originalLocation = storageProperties.getOriginalLocation() ;
          String cacheLocation= storageProperties.getCacheLocation();


        SafeDimension safeDimension =  validationService.getSafeDimensions(requestedW , requestedH );

        String cacheName = namingService.ConstructName(imageSystemName, safeDimension.width(), safeDimension.height());
        Path cachePath = storageService.getTargetPath(cacheName, cacheLocation );

        if (cacheService.exists(cacheName, cachePath))  return cacheService.read(cachePath);

        Path sourcePath = storageService.getTargetPath(imageSystemName, originalLocation );
        if (!Files.exists(sourcePath))  throw new ImageNotFoundException("File not found on disk: " + imageSystemName);

        BufferedImage original = ImageIO.read(sourcePath.toFile());

        BufferedImage resized = imageProcessor.process(original, safeDimension.width(), safeDimension.height());

        String format = imageSystemName.substring(imageSystemName.lastIndexOf(".") + 1).toLowerCase();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(resized, format, outputStream);

        byte[] result = outputStream.toByteArray();
        Files.write(cachePath, result);

        return result;
    }

}
