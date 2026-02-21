package com.image.quickimage.image.dto;

import java.util.List;

public record ColorPaletteResponse(String primary, String vibrant, List<String> palette) {}