package com.image.quickimage.image.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public")
@Tag(name = "System Discovery", description = "Unrestricted public endpoints for service health and connectivity checks.")
public class PublicEndpoint {

    @GetMapping("/info")
    @Operation(
            summary = "Service Connectivity Check",
            description = "A lightweight, unrestricted endpoint used to verify service availability. Unlike the transformation endpoints, this does not consume your AI or Rate-Limit tokens.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Service is reachable")
            }
    )
    public ResponseEntity<String> info() {
        return ResponseEntity.ok("Even this new URL is protected now!");
    }
}
