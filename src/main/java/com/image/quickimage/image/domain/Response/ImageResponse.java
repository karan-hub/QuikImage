package com.image.quickimage.image.domain.Response;

import com.image.quickimage.image.dto.ColorPaletteResponse;

import java.util.List;

public record ImageResponse(
        byte[] data,
        String contentType,
        String fileName,
        ColorPaletteResponse colorPalette
) {
}
