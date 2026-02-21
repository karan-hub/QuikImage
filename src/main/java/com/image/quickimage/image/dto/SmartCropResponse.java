package com.image.quickimage.image.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmartCropResponse {
    private int x;
    private int y;
    private int width;
    private int height;
}