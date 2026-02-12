package com.image.quickimage.image.service;

import com.image.quickimage.image.domain.SafeDimension;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class ValidationService {
    private static final int MAX = 2000;
    private static final int MIN = 10;
    private static final int ORIGINAL_WIDTH = 500;
    private static final int ORIGINAL_HEIGHT= 500;

     public  SafeDimension  getSafeDimensions(Integer requestedW, Integer requestedH) throws InvalidDimensionException {

         int w = (requestedW == null || requestedW <= 0) ? ORIGINAL_WIDTH : requestedW;
         int h = (requestedH == null || requestedH <= 0) ? ORIGINAL_HEIGHT : requestedH;

         int safeW = Math.max(MIN, Math.min(MAX, w));
         int safeH = Math.max(MIN, Math.min(MAX, h));

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
