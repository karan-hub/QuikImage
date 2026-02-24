package com.image.ai.ai.vision.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@AllArgsConstructor
public class SmartCropResponse {
    private int x;
    private int y;
    private int width;
    private int height;
}
