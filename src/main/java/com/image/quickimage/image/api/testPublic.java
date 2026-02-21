package com.image.quickimage.image.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public")
public class testPublic {

    @GetMapping("/info")
    public ResponseEntity<String> info() {
        return ResponseEntity.ok("Even this new URL is protected now!");
    }
}
