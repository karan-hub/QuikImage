package com.image.quickimage.image.infrastructure;

import com.image.quickimage.image.config.StorageProperties;
import com.image.quickimage.image.dto.FileUploadRequest;
import com.image.quickimage.image.exception.DuplicateNameException;
import com.image.quickimage.image.exception.UnsupportedMediaException;
import com.image.quickimage.image.model.ImageEntity;
import com.image.quickimage.image.repository.ImageRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@AllArgsConstructor
public class StorageService {
    private ImageRepository imageRepository;
    private StorageProperties storageProperties;
    public  boolean exists(String filename , Path targetPath) {
        if (Files.exists(targetPath)){
            System.out.println("Cache Hit! ");
             return  true;
        }else {
            return  false;
        }
    }


// Path Handling
    public  Path getTargetPath(String filename , String location) throws FileNotFoundException {
        try{
            Path path = Paths.get(location);
            Files.createDirectories(path);
             return path.resolve(filename);

        }catch (Exception e){
            throw  new FileNotFoundException("File not Found");
        }
    }

//    read(String filename)

    public String saveOriginal(FileUploadRequest request)   {
        MultipartFile image = request.image();

        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new UnsupportedMediaException("Only image files are allowed");
        }


        String baseName = (request.name() != null && !request.name().isBlank())
                ? request.name()
                : "image";
        baseName = baseName.replaceAll("[^a-zA-Z0-9-]", "_");

        if (imageRepository.existsByName(baseName))
            throw new DuplicateNameException("Database error: This name is already taken or invalid. : " + baseName );


        String originalName = image.getOriginalFilename();

        String extension = originalName != null && originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf("."))
                : ".jpg";
        String fileName = UUID.randomUUID().toString().substring(0, 8) + extension;


        String originalLocation = storageProperties.getOriginalLocation();
        Path uploadDir = Paths.get(originalLocation);

        try (InputStream in = image.getInputStream()) {
            Files.createDirectories(uploadDir);
            Path targetPath = uploadDir.resolve(fileName);
            Files.copy( in, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file on disk" ,e);
        }


        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setName(baseName);
        imageEntity.setSystemName(fileName);
        imageEntity.setContentType(image.getContentType());
        imageRepository.save(imageEntity);

        return "Image uploaded successfully: " + baseName;
    }


    public Resource getImage(String name) throws Exception {

        Path path = Paths.get("uploads").resolve(name);

        if (!Files.exists(path)) {
            throw new RuntimeException("File Not Found");
        }

        return new UrlResource(path.toUri());
    }

    public MediaType getImageContentType(String filename){

        String lowerName = filename.toLowerCase();
        if (lowerName.endsWith(".png")) return MediaType.IMAGE_PNG;
        if (lowerName.endsWith(".gif")) return MediaType.IMAGE_GIF;
        return MediaType.IMAGE_JPEG;

//        try {
//            String contentType = Files.probeContentType(path);
//            return  MediaType.parseMediaType(contentType != null ? contentType : "image/jpeg");
//        } catch (IOException e) {
//            return  MediaType.IMAGE_JPEG;
//        }

    }

}
