package com.image.quickimage.image.domain;

import com.image.quickimage.image.model.ImageEntity;
import com.image.quickimage.image.repository.ImageRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ImageNamingService {

private ImageRepository repository;

    public ImageNamingResult resolveNaming(ImageEntity entity, String requestedFormat, int w, int h) {
        String systemName = entity.getSystemName().toLowerCase().trim();
        int dotIndex = systemName.lastIndexOf(".");

        String baseUuid = (dotIndex == -1) ? systemName : systemName.substring(0, dotIndex);

        String originalExt = (dotIndex == -1) ? "jpg" : systemName.substring(dotIndex + 1);

        String finalExt = (requestedFormat != null && !requestedFormat.isEmpty())
                ? requestedFormat.toLowerCase().trim().replace(".", "")
                : originalExt;

        String cacheFileName = String.format("%s_w%d_h%d.%s", baseUuid, w, h, finalExt);

        return new ImageNamingResult(entity.getName(), finalExt, cacheFileName);
    }

    public String generateUniqueFriendlyName(String rawName) {
         if (rawName == null || rawName.isBlank()) {
            rawName = "image";
        }

         int lastDot = rawName.lastIndexOf(".");
        String nameWithoutExt = (lastDot > 0) ? rawName.substring(0, lastDot) : rawName;

        String base = nameWithoutExt
                .replaceAll("[^a-zA-Z0-9-]", "_")
                .toLowerCase();

         if (base.isBlank()) {
            base = "image";
        }

        String finalName = base;
        int count = 1;
        while (repository.existsByName(finalName)) {
            finalName = base + "_" + count++;
        }
        return finalName;
    }

    public String generateInternalSystemName(String originalFilename) {

        String ext = originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase()
                : ".jpg";

         return UUID.randomUUID().toString().replace("-", "").substring(0, 16) + ext;
    }

    public String determineFormat(String originalExt, String acceptHeader) {
        if (acceptHeader != null && acceptHeader.contains("image/webp")) {
            return "webp";
        }
        return originalExt.toLowerCase();
    }

    public String generateRedisKey(String baseUuid, int w, int h, int q, String format) {
        return String.format("img:%s:w%d:h%d:q%d:%s",
                baseUuid, w, h, q, format.toLowerCase().replace(".", ""));
    }


    public  String ConstructName(String systemName, int targetW , int  targetH ) throws IOException {

        String cleanName = systemName.toLowerCase().trim();

        int dotIndex = cleanName.lastIndexOf(".");
        String friendlyName;
        String targetFormat;

        if (dotIndex >0){
            friendlyName = cleanName.substring( 0,dotIndex);
            targetFormat = cleanName.substring(dotIndex);
        } else if (dotIndex == 0) {
            friendlyName = UUID.randomUUID().toString().substring(0, 8);
            targetFormat = cleanName.substring(dotIndex);
        } else {
            friendlyName = cleanName;
            targetFormat = ".jpg";
        }
        return String.format("%s_w%d_h%d%s", friendlyName, targetW, targetH, targetFormat);

    }
}
