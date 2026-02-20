package com.image.quickimage.image.infrastructure;

import com.image.quickimage.image.domain.Response.ImageResponse;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class CacheService {
//    Cache logic
public ImageResponse read(Path path) throws IOException {
    byte[] allBytes = Files.readAllBytes(path);
    String fileName = path.getFileName().toString();
    String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

    String contentType = "image/" + extension.toLowerCase();

    return new ImageResponse(allBytes, contentType, fileName);
}

        public  boolean exists(String filename , Path targetPath) {
                            return   Files.exists(targetPath) ;
            }

    public void write(Path path, byte[] content) throws IOException {
        Files.write(path, content);
    }

}
