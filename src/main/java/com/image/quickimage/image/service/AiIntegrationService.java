package com.image.quickimage.image.service;

import com.image.quickimage.image.dto.ColorPaletteResponse;
import com.image.quickimage.image.dto.SmartCropResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class AiIntegrationService {
    private  final WebClient  webClient ;

    public Mono<SmartCropResponse> getSmartCropCoordinates(byte[] imageBytes){
        return  webClient.post()
              .uri("/crop")
                .contentType(MediaType.IMAGE_JPEG)
                .bodyValue(imageBytes)
                .retrieve()
                .bodyToMono(SmartCropResponse.class);
    }

    public Mono<ColorPaletteResponse> extractColors(byte[] imageBytes) {
        return webClient.post()
               .uri("/colors")
                .contentType(MediaType.IMAGE_JPEG)
                .bodyValue(imageBytes)
                .retrieve()
                .bodyToMono(ColorPaletteResponse.class);
    }
}
