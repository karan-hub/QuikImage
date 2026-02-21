package com.image.quickimage.image.domain;

import com.image.quickimage.image.dto.ColorPaletteResponse;

import java.io.Serializable;

public record CachedImageBundle(
        byte[] imageData,
        ColorPaletteResponse colors
) implements Serializable {}
