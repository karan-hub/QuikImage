package com.image.quickimage.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "AI-extracted color profiles")
public record ColorPaletteResponse(
        @Schema(example = "#c9a18d") String primary,
        @Schema(example = "#d77549") String vibrant,
        List<String> palette
) {}