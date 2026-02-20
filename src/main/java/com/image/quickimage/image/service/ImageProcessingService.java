package com.image.quickimage.image.service;

import javax.imageio.spi.IIORegistry;

import com.image.quickimage.image.config.ImageProperties;
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
    private final ImageProperties imageProperties;
    private final RedisCacheService redisCacheService;




    public ImageProcessingService(StorageService storageService,
                                  ImageNamingService namingService,
                                  ImageProcessor imageProcessor,
                                  CacheService cacheService,
                                  ValidationService validationService,
                                  StorageProperties storageProperties, ImageRepository imageRepository, ImageProperties imageProperties, RedisCacheService redisCacheService) {
        this.storageService = storageService;
        this.namingService = namingService;
        this.imageProcessor = imageProcessor;
        this.cacheService = cacheService;
        this.validationService = validationService;
        this.storageProperties = storageProperties;
        this.imageRepository = imageRepository;
        this.imageProperties = imageProperties;
        this.redisCacheService = redisCacheService;
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


    public ImageResponse getProcessedImage(String friendlyName, String requestedExt ,Integer requestedW, Integer requestedH, Integer quality ,String acceptHeader) throws Exception {

        ImageEntity imageInfo = imageRepository.findByName(friendlyName)
                .orElseThrow(() -> new ImageNotFoundException("Image not found in DB : " + friendlyName));

        String finalExt = namingService.determineFormat(requestedExt, acceptHeader);
        SafeDimension safeDimension = validationService.getSafeDimensions(requestedW, requestedH);

        int finalQualityInt = (quality != null) ? quality : (int)(imageProperties.defaultQuality() * 100);
        float qualityFactor = (float) finalQualityInt / 100.0f;
        qualityFactor = Math.max(imageProperties.minQuality(), Math.min(imageProperties.maxQuality(), qualityFactor));


        String systemName = imageInfo.getSystemName();
        String baseUuid = systemName.substring(0, systemName.lastIndexOf("."));
//        String cacheKey = namingService.generateRedisKey(baseUuid, safeDimension.width(), safeDimension.height(), quality, finalExt);

        ImageNamingResult namingResult = namingService.resolveNaming(
                imageInfo,
                finalExt,
                safeDimension.width(),
                safeDimension.height()
        );

        String redisKey = namingService.generateRedisKey(
                namingResult.systemNameWithoutExtension(),
                safeDimension.width(),
                safeDimension.height(),
                quality,
                namingResult.outputExtension()
        );


        byte[] cachedData = redisCacheService.get(redisKey);
        String mimeType = (finalExt.equalsIgnoreCase("jpg") || finalExt.equalsIgnoreCase("jpeg")) ? "image/jpeg" : "image/" + finalExt;


        if (cachedData != null) {
            System.out.println("🚀 REDIS HIT: Serving " + redisKey);
            return new ImageResponse(cachedData, mimeType, redisKey);
        }


        System.out.println("🐢 REDIS MISS: Processing " + friendlyName);
        Path sourcePath = storageService.getTargetPath(imageInfo.getSystemName(), storageProperties.getOriginalLocation());

        if (!Files.exists(sourcePath))
            throw new ImageNotFoundException("Original file missing on disk: " + imageInfo.getSystemName());



        BufferedImage original = ImageIO.read(sourcePath.toFile());
        BufferedImage resized = imageProcessor.process(original, safeDimension.width(), safeDimension.height());

        byte[] resultBytes = compressToByteArray(resized, finalExt, qualityFactor);

        redisCacheService.save(redisKey, resultBytes);

        return new ImageResponse(resultBytes, mimeType, namingResult.systemNameWithoutExtension());
    }

    private byte[] compressToByteArray(BufferedImage image, String format, float quality) throws IOException {
        String cleanFormat = format.toLowerCase().trim().replace(".", "");
        if (cleanFormat.equals("jpg")) cleanFormat = "jpeg";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(cleanFormat);

        if (!writers.hasNext()) throw new IOException("No writer for: " + cleanFormat);

        ImageWriter writer = writers.next();
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
            writer.setOutput(ios);
            ImageWriteParam param = writer.getDefaultWriteParam();

            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);


                String[] types = param.getCompressionTypes();
                if (types != null && types.length > 0) {
                    param.setCompressionType(types[0]);
                }


                param.setCompressionQuality(quality);
            }

            writer.write(null, new IIOImage(image, null, null), param);
            ios.flush();
        } finally {
            writer.dispose();
        }
        return baos.toByteArray();
    }

 }
