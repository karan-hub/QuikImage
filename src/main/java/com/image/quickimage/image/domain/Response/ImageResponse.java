package com.image.quickimage.image.domain.Response;

import com.image.quickimage.image.dto.ColorPaletteResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Comprehensive image response including AI insights")
public record ImageResponse(
        @Schema(description = "Base64 encoded image data (only if extractColors=true)") byte[] data,
        @Schema(example = "image/webp") String contentType,
        @Schema(example = "mumbai_optimized") String fileName,
        ColorPaletteResponse colorPalette
) {}