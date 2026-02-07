package com.image.quickimage.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageNamingService {
    public  String ConstructName(String imageName ,  int targetW , int  targetH ) throws IOException {
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
        return  baseName + "_w" + targetW + "_h" + targetH + extension;

    }
}
