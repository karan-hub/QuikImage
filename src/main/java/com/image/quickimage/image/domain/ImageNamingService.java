package com.image.quickimage.image.domain;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class ImageNamingService {
//    Naming logic
    public  String ConstructName(String systemName, int targetW , int  targetH ) throws IOException {

        String cleanName = systemName.toLowerCase().trim();

        int dotIndex = cleanName.lastIndexOf(".");
        String baseName;
        String extension;

        if (dotIndex >0){
            baseName = cleanName.substring( 0,dotIndex);
            extension = cleanName.substring(dotIndex);
        } else if (dotIndex == 0) {
            baseName= UUID.randomUUID().toString().substring(0, 8);
            extension = cleanName.substring(dotIndex);
        } else {
            baseName = cleanName;
            extension = ".jpg";
        }
        return String.format("%s_w%d_h%d%s", baseName, targetW, targetH, extension);

    }
}
