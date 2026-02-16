package com.image.quickimage.image.service;

import com.image.quickimage.image.config.ImageProperties;
import com.image.quickimage.image.domain.SafeDimension;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {
    private   final ImageProperties imageProperties ;

    public ValidationService(ImageProperties imageProperties) {
        this.imageProperties = imageProperties;
    }

    public SafeDimension getSafeDimensions(Integer requestedW, Integer requestedH) {

         int w = (requestedW == null || requestedW <= 0) ? imageProperties.originalWidth() : requestedW;
        int h = (requestedH == null || requestedH <= 0) ? imageProperties.originalHeight() : requestedH;


        int safeW = Math.max(imageProperties.min(), Math.min(imageProperties.max(), w));
        int safeH = Math.max(imageProperties.min(), Math.min(imageProperties.max(), h));

        return new SafeDimension(safeW, safeH);
    }
//
//    METHOD validateFileType(File file):
//    // Use ImageIO to check if it can actually read the file
//    // rather than just trusting the .jpg string
//    public  boolean validateFileType(File file){
//
//    }

}
