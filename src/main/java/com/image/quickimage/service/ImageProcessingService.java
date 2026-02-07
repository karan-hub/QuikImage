package com.image.quickimage.service;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageProcessingService {

    public Resource processImage(String name) throws IOException {
        // 1. Use clean paths (removed the leading space bug!)
        Path originalPath = Paths.get("D:/quickimage/uploads", name);

        BufferedImage original = ImageIO.read(originalPath.toFile());
        if (original == null) {
            throw new IOException("Could not read image: " + originalPath);
        }

        // 2. Create target image
        BufferedImage resized = new BufferedImage(
                200,
                200,
                BufferedImage.TYPE_4BYTE_ABGR
        );
        Graphics2D g = resized.createGraphics();

        try {
            // 3. Fix transparency: Fill background with white
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, 200, 200);

            // 4. High-quality scaling
            g.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC
            );
            g.drawImage(original, 0, 0, 200, 200, null);
        } finally {
            g.dispose(); // Always dispose graphics resources
        }

        // 5. Save and Return
        Path outputDir = Paths.get("processed");
        Files.createDirectories(outputDir);

        String outputName = name.replace(".png", ".jpg");
        Path outputPath = outputDir.resolve(outputName);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        boolean written = ImageIO.write(resized, "png", outputStream );

        if (!written) {
            throw new IOException("Failed to encode image as JPG");
        }

        return new FileSystemResource(outputPath);
    }

    public byte[] getProcessedImage(String filename, Integer targetW, Integer targetH) throws IOException {

        Path sourcePath = Paths.get("D:/quickimage/originals").resolve(filename);
        if (!Files.exists(sourcePath)) throw new FileNotFoundException("File not found");

        BufferedImage original = ImageIO.read(sourcePath.toFile());

        int finalW = (targetW == null || targetW <= 0) ? original.getWidth() : targetW;
        int finalH = (targetH == null || targetH <= 0) ? original.getHeight() : targetH;

         double ratio = Math.min((double) finalW / original.getWidth(), (double) finalH / original.getHeight());

        int actualW = (int) (original.getWidth() * ratio);
        int actualH = (int) (original.getHeight() * ratio);

        BufferedImage output = new BufferedImage(
                actualW,
                actualH,
                BufferedImage.TYPE_INT_RGB
        );
        Graphics2D g = output.createGraphics();

        try {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, actualW, actualH);

             g.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC
            );
            g.drawImage(original, 0, 0, actualW, actualH, null);
        } finally {
            g.dispose();
        }
        ByteArrayOutputStream  outputStream = new ByteArrayOutputStream();
        ImageIO.write(output ,"JPG",outputStream);
        return  outputStream.toByteArray();
    }
}
