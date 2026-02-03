package com.image.quickimage.service;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@Service
public class ImageProcessingService {

    public Resource processImage(String name) throws IOException {

        Path originalPath =
                Paths.get("D:\\quickimage\\uploads", name);
        BufferedImage original = ImageIO.read(new File(originalPath.toUri()));

        if (original == null) {
            throw new RuntimeException("Invalid image file" + originalPath);
        }

        BufferedImage resized = new BufferedImage(
                200,
                200,
                BufferedImage.TYPE_INT_RGB // ✅ correct for JPG
        );

        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC
        );
        g.drawImage(original, 0, 0, 200, 200, null);
        g.dispose();

        Path outputDir = Paths.get("processed");
        Files.createDirectories(outputDir);

        String outputName = name.replace(".png", ".jpg");
        Path outputPath = outputDir.resolve(outputName);

        boolean written = ImageIO.write(resized, "jpg", outputPath.toFile());
        System.out.println("Image written: " + written);

        if (!written || !Files.exists(outputPath)) {
            throw new RuntimeException("Processed image not found: " + outputPath);
        }

        return new FileSystemResource(outputPath);
    }
}
