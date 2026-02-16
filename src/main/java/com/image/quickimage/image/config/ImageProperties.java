package com.image.quickimage.image.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@ConfigurationProperties(prefix = "app.image")
public record ImageProperties(
             int max ,
              int min,
              int originalWidth,
              int originalHeight
) {
}
