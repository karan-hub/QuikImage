package com.image.quickimage.image.domain;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class ImageNamingService {
//    Naming logic
    public  String ConstructName(String imageName,  int targetW , int  targetH ) throws IOException {
        String cleanName = imageName.toLowerCase().trim();

        cleanName = new File(cleanName).getName();

        int dotIndex = imageName.lastIndexOf(".");
        String baseName;
        String extension;

        if (dotIndex >0){
            baseName = imageName.substring( 0,dotIndex);
            extension = imageName.substring(dotIndex);
        }else {
            baseName = imageName;
            extension = ".jpg";
        }
        return String.format("%s_w%d_h%d%s", baseName, targetW, targetH, extension);

    }
}
