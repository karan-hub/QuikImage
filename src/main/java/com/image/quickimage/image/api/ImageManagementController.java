package com.image.quickimage.image.api;

import com.image.quickimage.image.dto.ManagedImageResponse;
import com.image.quickimage.image.dto.PaginatedResponse;
import com.image.quickimage.image.repository.ImageRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/management")
@AllArgsConstructor
@Tag(name = "Image Management", description = "Administrative endpoints for asset discovery, library pagination, and metadata retrieval.")
public class ImageManagementController {

    private final ImageRepository repository;

    @Operation(
            summary = "Retrieve Asset Library",
            description = "Returns a paginated list of all uploaded images. Each entry includes a pre-signed transformation URL for easy previewing in galleries.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved image page"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access")
            }
    )
    @GetMapping("/images")
    public PaginatedResponse<ManagedImageResponse> getAllImages(Pageable pageable) {
        Page<ManagedImageResponse> page = repository.findAll(pageable)
                .map(entity -> new ManagedImageResponse(
                        entity.getId(),
                        entity.getName(),
                        entity.getContentType(),
                        String.format("/api/v1/image/%s.jpg?w=500&h=500&q=80", entity.getName())));

        return new PaginatedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }
}
