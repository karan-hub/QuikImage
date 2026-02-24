package com.image.quickimage.image.api;

import com.image.quickimage.image.dto.ManagedImageResponse;
import com.image.quickimage.image.dto.PaginatedResponse;
import com.image.quickimage.image.repository.ImageRepository;
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
@CrossOrigin(origins = "*")
public class ImageManagementController {

    private final ImageRepository repository;

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
