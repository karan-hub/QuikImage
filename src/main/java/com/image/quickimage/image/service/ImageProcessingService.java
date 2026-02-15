package com.image.quickimage.image.service;

import javax.imageio.spi.IIORegistry;
import jakarta.annotation.PostConstruct;

import com.image.quickimage.image.domain.ImageNamingResult;
import com.image.quickimage.image.domain.ImageNamingService;
import com.image.quickimage.image.domain.ImageProcessor;
import com.image.quickimage.image.domain.Response.ImageResponse;
import com.image.quickimage.image.domain.SafeDimension;
import com.image.quickimage.image.config.StorageProperties;
import com.image.quickimage.image.exception.ImageNotFoundException;
import com.image.quickimage.image.infrastructure.CacheService;
import com.image.quickimage.image.infrastructure.StorageService;
import com.image.quickimage.image.model.ImageEntity;
import com.image.quickimage.image.repository.ImageRepository;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;

@Service
@ConfigurationProperties(prefix = "app.storage")
public class ImageProcessingService {
    static {
        System.out.println("test ImageIO");
        Thread.currentThread().setContextClassLoader(ImageProcessingService.class.getClassLoader());
        ImageIO.scanForPlugins();
    }

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

    @PostConstruct
    public void init() {
        try {
            IIORegistry registry = IIORegistry.getDefaultInstance();

            // We look for the provider using its name as a String
            // This prevents the "cannot find symbol" compile error!
            Class<?> writerClass = Class.forName("com.twelvemonkeys.imageio.plugins.webp.WebPImageWriterSpi");
            Object writerInstance = writerClass.getDeclaredConstructor().newInstance();

            registry.registerServiceProvider(writerInstance);

            System.out.println("✅ WebP registered via reflection!");
        } catch (Exception e) {
            System.out.println("⚠️ WebP manual load skipped: " + e.getMessage());
        }

        System.out.println("🚀 Available Writers: " + Arrays.toString(ImageIO.getWriterFormatNames()));
    }

    public ImageResponse getProcessedImage(String fileName, Integer requestedW, Integer requestedH, Integer quality) throws Exception {


        int dotIndex = fileName.lastIndexOf(".");
        String nameOnly = (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
        String requestedExt = (dotIndex == -1) ? null : fileName.substring(dotIndex + 1);


        ImageEntity imageInfo = imageRepository.findByName(nameOnly)
                .orElseThrow(() -> new ImageNotFoundException("Image not found in DB: " + nameOnly));


        SafeDimension safeDimension = validationService.getSafeDimensions(requestedW, requestedH);
        ImageNamingResult resolvedName = namingService.resolveNaming(imageInfo, requestedExt, safeDimension.width(), safeDimension.height());

        Path cachePath = storageService.getTargetPath(resolvedName.cacheFileName(), storageProperties.getCacheLocation());

         if (Files.exists(cachePath))
             return cacheService.read(cachePath);



        Path sourcePath = storageService.getTargetPath(imageInfo.getSystemName(), storageProperties.getOriginalLocation());
        if (!Files.exists(sourcePath))
            throw new ImageNotFoundException("Original file missing on disk: " + imageInfo.getSystemName());



        BufferedImage original = ImageIO.read(sourcePath.toFile());
        BufferedImage resized = imageProcessor.process(original, safeDimension.width(), safeDimension.height());


        float qualityFactor = (quality != null) ? quality / 100.0f : 0.8f;
        String format = resolvedName.outputExtension().replace(".", "");


        writeCompressedImage(resized, format, qualityFactor, cachePath);
        byte[] resultBytes = Files.readAllBytes(cachePath);
        String mimeType = "image/" + format;
        return new ImageResponse(resultBytes, mimeType, resolvedName.cacheFileName());
    }


    private void writeCompressedImage(BufferedImage image, String format, float quality, Path targetPath) throws IOException {
        String cleanFormat = format.toLowerCase().trim().replace(".", "");
        if (cleanFormat.equals("jpg")) cleanFormat = "jpeg";

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(cleanFormat);
        if (!writers.hasNext()) throw new IOException("No writer for: " + cleanFormat);

        ImageWriter writer = writers.next();
        ImageWriteParam param = writer.getDefaultWriteParam();

        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

            // Fix for "No compression type set!"
            String[] compressionTypes = param.getCompressionTypes();
            if (compressionTypes != null && compressionTypes.length > 0) {
                // For WebP, index 0 is typically "Lossy"
                param.setCompressionType(compressionTypes[0]);
            }

            param.setCompressionQuality(quality);
        }

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(targetPath.toFile())) {
            writer.setOutput(ios);
            writer.write(null, new IIOImage(image, null, null), param);
        } finally {
            writer.dispose();
        }
    }
}
