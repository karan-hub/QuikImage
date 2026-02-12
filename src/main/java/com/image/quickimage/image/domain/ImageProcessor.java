package com.image.quickimage.image.domain;

import java.awt.image.BufferedImage;

public interface ImageProcessor {
    BufferedImage process(BufferedImage input,int targetW, int targetH);
}
