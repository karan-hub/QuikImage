package com.image.quickimage.image.domain.Response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;


@Schema(description = "Standard error structure for failed requests")
public record ErrorResponse(
        LocalDateTime timestamp,
        @Schema(example = "429") int status,
        @Schema(example = "Too Many Requests") String error,
        @Schema(example = "Rate limit exceeded. Please wait 30 seconds.") String message,
        @Schema(example = "/api/v1/image/upload") String path
) {}
