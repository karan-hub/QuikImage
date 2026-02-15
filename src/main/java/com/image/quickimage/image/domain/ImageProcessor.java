package com.image.quickimage.image.domain;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public interface ImageProcessor {
     default  BufferedImage process(BufferedImage input, int targetW, int targetH) throws IOException {
         double ratio = Math.min(
                 (double) targetW /  input.getWidth(),
                 (double) targetH /  input.getHeight()
         );
         ratio = Math.min(ratio, 1.0);

         int actualW = (int) ( input.getWidth() * ratio);
         int actualH = (int) ( input.getHeight() * ratio);

         BufferedImage outputImage = new BufferedImage(
                 actualW,
                 actualH,
                 BufferedImage.TYPE_INT_ARGB
         );

         Graphics2D g = outputImage.createGraphics();
         try {

             g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
             g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
             g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

             g.drawImage( input, 0, 0, actualW, actualH, null);

         } finally {
             g.dispose();
         }
         return outputImage;
     }
}
