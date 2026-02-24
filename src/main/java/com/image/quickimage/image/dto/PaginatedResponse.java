package com.image.quickimage.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Standardized paginated wrapper")
public record PaginatedResponse<T>(
        List<T> content,
        @Schema(example = "0") int page,
        @Schema(example = "20") int size,
        @Schema(example = "150") long totalElements,
        @Schema(example = "8") int totalPages,
        @Schema(example = "false") boolean last
) {}