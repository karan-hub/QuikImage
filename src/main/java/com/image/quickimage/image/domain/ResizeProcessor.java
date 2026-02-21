package com.image.quickimage.image.domain;

import com.image.quickimage.image.dto.SmartCropResponse;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Component
@Primary
public class ResizeProcessor implements ImageProcessor {

        @Override
        public BufferedImage process(BufferedImage input, int targetW, int targetH) throws IOException {
                int type = input.getTransparency() == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB
                                : BufferedImage.TYPE_INT_RGB;
                return Thumbnails.of(input)
                                .size(targetW, targetH)
                                .outputQuality(1.0f)
                                .imageType(type)
                                .asBufferedImage();
        }

        @Override
        public BufferedImage processSmart(BufferedImage input, SmartCropResponse crop, int targetW, int targetH)
                        throws IOException {
                // 1. Determine the image type (Alpha support)
                int type = input.getTransparency() == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB
                                : BufferedImage.TYPE_INT_RGB;

                return Thumbnails.of(input)
                                .sourceRegion(crop.getX(), crop.getY(), crop.getWidth(), crop.getHeight())
                                .size(targetW, targetH)
                                .imageType(type)
                                .outputQuality(1.0f)
                                .asBufferedImage();
        }

}
