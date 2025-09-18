package com.origin.platform.urlshortener.mapper;

import com.origin.platform.urlshortener.dto.response.ShortenUrlResponse;
import com.origin.platform.urlshortener.dto.response.UrlInfoResponse;
import com.origin.platform.urlshortener.model.UrlMapping;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UrlMapperTest {
    private final UrlMapper urlMapper = new UrlMapper();

    @Test
    void whenValidOriginalUrlAndShortUrl_thenReturnsShortenUrlResponse() {
        String originalUrl = "https://example.com";
        String shortUrl = "http://short.ly/abc123";
        ShortenUrlResponse response = urlMapper.toShortenUrlResponse(originalUrl, shortUrl);
        assertNotNull(response);
        assertEquals(originalUrl, response.getOriginalUrl());
        assertEquals(shortUrl, response.getShortUrl());
    }

    @Test
    void testToUrlInfoResponse() {
        OffsetDateTime now = OffsetDateTime.now();
        UrlMapping mapping = UrlMapping.builder()
                .originalUrl("https://example.com")
                .shortCode("abc123")
                .createdAt(now)
                .hitCount(42)
                .build();
        UrlInfoResponse response = urlMapper.toUrlInfoResponse(mapping);
        assertNotNull(response);
        assertEquals("https://example.com", response.getOriginalUrl());
        assertEquals("abc123", response.getShortCode());
        assertEquals(now, response.getCreatedAt());
        assertEquals(42, response.getHitCount());
    }

    @Test
    void whenNullOriginalUrl_thenThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> urlMapper.toShortenUrlResponse(null, "short"));
        assertTrue(ex.getMessage().contains("originalUrl"));
    }

    @Test
    void whenNullShortUrl_thenThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> urlMapper.toShortenUrlResponse("original", null));
        assertTrue(ex.getMessage().contains("shortUrl"));
    }

    @Test
    void testToUrlInfoResponse_NullMapping() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> urlMapper.toUrlInfoResponse(null));
        assertTrue(ex.getMessage().contains("mapping"));
    }
}
