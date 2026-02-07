package com.image.quickimage.service;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class ImageProcessingService {

    private ImageNamingService namingService;
    private  StorageService storageService;

    public byte[] getProcessedImage(String filename, Integer targetW, Integer targetH) throws IOException {
        filename = filename.toLowerCase();
//        D:/quickimage/cached
//        D:/quickimage/originals
        String  path = "D:/quickimage/originals";

        Path sourcePath = storageService.getTargetPath(filename, path)
        if (!Files.exists(sourcePath))
            throw new FileNotFoundException("File not found");

        String cacheName = namingService.ConstructName(filename, targetW, targetH);

        Path targetPath = storageService.getTargetPath(cacheName);

        if (storageService.exists(cacheName , targetPath)){
              return  Files.readAllBytes(targetPath);
         }


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

        byte[] finalBytes = outputStream.toByteArray();
        Files.write(targetPath, finalBytes);

        return finalBytes ;
    }
}
