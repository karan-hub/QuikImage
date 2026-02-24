package com.image.ai.ai.vision.service.service;

import com.image.ai.ai.vision.service.dto.SmartCropResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class VisionService {

    @Value("${hf.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://router.huggingface.co/hf-inference/models/facebook/detr-resnet-50")
            .build();

    public SmartCropResponse getCrop(byte[] imageBytes) {
        // 1. Call Hugging Face API
        List<Map<String, Object>> detections = webClient.post()
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "image/jpeg")
                .header("X-Wait-For-Model", "true")
                .bodyValue(imageBytes)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                })
                .block();

        if (detections == null || detections.isEmpty()) {
            return new SmartCropResponse(0, 0, 500, 500); // Default fallback
        }

        // 2. Logic: Prioritize "person" detection for Face Detection feature
        Map<String, Object> bestDetection = detections.stream()
                .filter(d -> ((Number) d.get("score")).doubleValue() > 0.5)
                .filter(d -> "person".equalsIgnoreCase((String) d.get("label")))
                .findFirst()
                .orElseGet(() -> {
                    // 3. Fallback: Pick the largest bounding box if no person found
                    return detections.stream()
                            .filter(d -> ((Number) d.get("score")).doubleValue() > 0.3)
                            .max((d1, d2) -> {
                                double area1 = calculateArea(d1);
                                double area2 = calculateArea(d2);
                                return Double.compare(area1, area2);
                            }).orElse(null);
                });

        if (bestDetection == null) {
            return new SmartCropResponse(0, 0, 500, 500);
        }

        return mapToBox(bestDetection);
    }

    @SuppressWarnings("unchecked")
    private double calculateArea(Map<String, Object> detection) {
        Map<String, Object> box = (Map<String, Object>) detection.get("box");
        double w = ((Number) box.get("xmax")).doubleValue() - ((Number) box.get("xmin")).doubleValue();
        double h = ((Number) box.get("ymax")).doubleValue() - ((Number) box.get("ymin")).doubleValue();
        return w * h;
    }

    @SuppressWarnings("unchecked")
    private SmartCropResponse mapToBox(Map<String, Object> detection) {
        Map<String, Object> box = (Map<String, Object>) detection.get("box");

        int x = ((Number) box.get("xmin")).intValue();
        int y = ((Number) box.get("ymin")).intValue();
        int xMax = ((Number) box.get("xmax")).intValue();
        int yMax = ((Number) box.get("ymax")).intValue();

        int w = xMax - x;
        int h = yMax - y;

        return new SmartCropResponse(x, y, w, h);
    }
}