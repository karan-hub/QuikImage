package com.image.quickimage.image.domain;

public record ImagePathComponents(
        String cacheBaseName,
        String extension,
        String fullCacheName
) {
}
