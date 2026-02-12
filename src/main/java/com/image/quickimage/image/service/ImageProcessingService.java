package com.image.quickimage.image.service;

import com.image.quickimage.image.domain.ImageNamingService;
import com.image.quickimage.image.domain.ImageProcessor;
import com.image.quickimage.image.domain.SafeDimension;
import com.image.quickimage.image.domain.StorageProperties;
import com.image.quickimage.image.exception.ImageNotFoundException;
import com.image.quickimage.image.infrastructure.CacheService;
import com.image.quickimage.image.infrastructure.StorageService;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
@ConfigurationProperties(prefix = "app.storage")
public class ImageProcessingService {

    private ImageNamingService namingService;
    private StorageService storageService;
    private final ImageProcessor imageProcessor;
    private final CacheService cacheService;
    private  final  ValidationService validationService;
    private final StorageProperties storageProperties;


    public ImageProcessingService(StorageService storageService,
                                  ImageNamingService namingService,
                                  ImageProcessor imageProcessor,
                                  CacheService cacheService,
                                  ValidationService validationService,
                                  StorageProperties storageProperties) {
        this.storageService = storageService;
        this.namingService = namingService;
        this.imageProcessor = imageProcessor;
        this.cacheService = cacheService;
        this.validationService = validationService;
        this.storageProperties = storageProperties;
    }

    public byte[] getProcessedImage(String filename, Integer requestedW, Integer requestedH) throws  Exception {

          String originalLocation = storageProperties.getOriginalLocation() ;
          String cacheLocation= storageProperties.getCacheLocation();


        SafeDimension safeDimension =  validationService.getSafeDimensions(requestedW , requestedH );

        String cacheName = namingService.ConstructName(filename, safeDimension.width(), safeDimension.height());
        Path cachePath = storageService.getTargetPath(cacheName, cacheLocation );
        if (cacheService.exists(cacheName, cachePath))  return cacheService.read(cachePath);


        Path sourcePath = storageService.getTargetPath(filename, originalLocation );
        if (!Files.exists(sourcePath))  throw new ImageNotFoundException("File not found");


        BufferedImage original = ImageIO.read(sourcePath.toFile());
        BufferedImage resized = imageProcessor.process(original, safeDimension.width(), safeDimension.height());


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(resized, "JPG", outputStream);

        byte[] result = outputStream.toByteArray();
        Files.write(cachePath, result);

        return result;
    }

}
