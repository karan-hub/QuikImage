package com.image.ai.ai.vision.service.Controller;

import com.image.ai.ai.vision.service.dto.ColorPaletteResponse;
import com.image.ai.ai.vision.service.dto.SmartCropResponse;
import com.image.ai.ai.vision.service.service.ColorExtractionService;
import com.image.ai.ai.vision.service.service.VisionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai/analyze")
public class AiController {

    private final VisionService visionService;
    private final ColorExtractionService colorService;

    public AiController(VisionService visionService, ColorExtractionService colorService) {
        this.visionService = visionService;
        this.colorService = colorService;
    }

    @PostMapping("/crop")
    public SmartCropResponse getCrop(@RequestBody byte[] bytes  ) {
        return visionService.getCrop(bytes);
    }

    @PostMapping("/colors")
    public ColorPaletteResponse getColors(@RequestBody byte[] bytes) {
        return colorService.extract(bytes);
    }
}