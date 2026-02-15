package com.image.quickimage.image.domain;

public record ImageNamingResult(
        String systemNameWithoutExtension,
        String outputExtension,
        String cacheFileName
) {
}
