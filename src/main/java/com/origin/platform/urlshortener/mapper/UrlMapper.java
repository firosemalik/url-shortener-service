package com.origin.platform.urlshortener.mapper;

import com.origin.platform.urlshortener.model.UrlMapping;
import com.origin.platform.urlshortener.dto.response.ShortenUrlResponse;
import com.origin.platform.urlshortener.dto.response.UrlInfoResponse;
import org.springframework.stereotype.Component;

@Component
public class UrlMapper {
    public ShortenUrlResponse toShortenUrlResponse(String originalUrl, String shortUrl) {
        if (originalUrl == null || originalUrl.isEmpty()) {
            throw new IllegalArgumentException("originalUrl must not be null");
        }
        if (shortUrl == null || shortUrl.isEmpty()) {
            throw new IllegalArgumentException("shortUrl must not be null");
        }

        return ShortenUrlResponse.builder()
                .shortUrl(shortUrl)
                .originalUrl(originalUrl)
                .build();
    }

    public UrlInfoResponse toUrlInfoResponse(UrlMapping mapping) {
        if (mapping == null) {
            throw new IllegalArgumentException("mapping must not be null");
        }
        return UrlInfoResponse.builder()
                .shortCode(mapping.getShortCode())
                .originalUrl(mapping.getOriginalUrl())
                .createdAt(mapping.getCreatedAt())
                .hitCount(mapping.getHitCount())
                .build();
    }
}
