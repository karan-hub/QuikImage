package com.image.quickimage.image.domain;

import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;

@Component
public class ResizeProcessor implements  ImageProcessor   {

    @Override
    public BufferedImage process(BufferedImage  input, int targetW, int targetH) {
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
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = outputImage.createGraphics();
        try {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, actualW, actualH);
            g.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC
            );

            g.drawImage( input, 0, 0, actualW, actualH, null);

        } finally {
            g.dispose();
        }
        return outputImage;
    }
}
