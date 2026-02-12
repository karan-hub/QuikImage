package com.image.quickimage.image.infrastructure;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class CacheService {
//    Cache logic
        public  boolean exists(String filename , Path targetPath) {
                            return   Files.exists(targetPath) ;
            }

    public byte[] read(Path path) throws IOException {
        return Files.readAllBytes(path);
    }

    public void write(Path path, byte[] content) throws IOException {
        Files.write(path, content);
    }

}
