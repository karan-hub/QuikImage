package com.image.ai.ai.vision.service.service;

import com.image.ai.ai.vision.service.dto.ColorPaletteResponse;
import de.androidpit.colorthief.ColorThief;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import java.util.ArrayList;
import java.util.List;

@Service
public class ColorExtractionService {

    public ColorPaletteResponse extract(byte[] imageBytes) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));

            // 1. Get the single dominant color (Primary)
            int[] primaryRgb = ColorThief.getColor(image);
            String primaryHex = convertToHex(primaryRgb);

            // 2. Get a palette of 8 colors for better selection options
            int[][] paletteRgb = ColorThief.getPalette(image, 8);

            List<String> hexPalette = new ArrayList<>();
            int[] bestVibrantRgb = primaryRgb;
            float maxVibrancy = -1;

            for (int[] rgb : paletteRgb) {
                String hex = convertToHex(rgb);
                hexPalette.add(hex);

                // 3. Logic: Pick the most 'vibrant' color based on HSL
                // Vibrant = High Saturation, balanced Lightness (not too dark, not too bright)
                float[] hsl = new float[3];
                rgbToHsl(rgb[0], rgb[1], rgb[2], hsl);

                float saturation = hsl[1];
                float lightness = hsl[2];

                // Reward colors with higher saturation
                // Penalize colors that are too dark (< 0.2) or too bright (> 0.8)
                float vibrancyScore = saturation;
                if (lightness < 0.2f || lightness > 0.8f) {
                    vibrancyScore *= 0.5f;
                }

                if (vibrancyScore > maxVibrancy) {
                    maxVibrancy = vibrancyScore;
                    bestVibrantRgb = rgb;
                }
            }

            String vibrantHex = convertToHex(bestVibrantRgb);

            return new ColorPaletteResponse(primaryHex, vibrantHex, hexPalette);

        } catch (Exception e) {
            // Reliable fallbacks
            return new ColorPaletteResponse("#757575", "#424242", List.of("#757575", "#424242"));
        }
    }

    private String convertToHex(int[] rgb) {
        if (rgb == null)
            return "#757575";
        return String.format("#%02x%02x%02x", rgb[0], rgb[1], rgb[2]);
    }

    /**
     * Converts RGB to HSL.
     * h (hue) is in [0, 360], s (saturation) and l (lightness) are in [0, 1].
     */
    private void rgbToHsl(int r, int g, int b, float[] hsl) {
        float rf = r / 255f;
        float gf = g / 255f;
        float bf = b / 255f;

        float max = Math.max(rf, Math.max(gf, bf));
        float min = Math.min(rf, Math.min(gf, bf));
        float h, s, l = (max + min) / 2f;

        if (max == min) {
            h = s = 0; // achromatic
        } else {
            float d = max - min;
            s = l > 0.5 ? d / (2 - max - min) : d / (max + min);
            if (max == rf) {
                h = (gf - bf) / d + (gf < bf ? 6 : 0);
            } else if (max == gf) {
                h = (bf - rf) / d + 2;
            } else {
                h = (rf - gf) / d + 4;
            }
            h /= 6;
        }
        hsl[0] = h * 360;
        hsl[1] = s;
        hsl[2] = l;
    }
}