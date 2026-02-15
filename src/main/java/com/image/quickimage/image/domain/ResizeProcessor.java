package com.image.quickimage.image.domain;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Component
@Primary
public class ResizeProcessor implements  ImageProcessor   {

    @Override
    public BufferedImage process(BufferedImage  input, int targetW, int targetH) throws IOException {
        return Thumbnails.of(input)
                .size(targetW, targetH)
                .outputQuality(1.0f)
                .asBufferedImage();
    }

}
