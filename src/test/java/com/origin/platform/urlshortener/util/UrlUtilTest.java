package com.origin.platform.urlshortener.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UrlUtilTest {
    @Test
    void testBuildShortUrl_withTrailingSlash() {
        String endpoint = "http://localhost:8080/";
        String code = "abc123";
        String result = UrlUtil.buildShortUrl(endpoint, code);
        assertEquals("http://localhost:8080/abc123", result);
    }

    @Test
    void testBuildShortUrl_withoutTrailingSlash() {
        String endpoint = "http://localhost:8080";
        String code = "xyz789";
        String result = UrlUtil.buildShortUrl(endpoint, code);
        assertEquals("http://localhost:8080/xyz789", result);
    }

    @Test
    void testBuildShortUrl_nullEndpoint() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                UrlUtil.buildShortUrl(null, "abc123")
        );
        assertTrue(ex.getMessage().contains("endpointPath"));
    }

    @Test
    void testBuildShortUrl_emptyEndpoint() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                UrlUtil.buildShortUrl("   ", "abc123")
        );
        assertTrue(ex.getMessage().contains("endpointPath"));
    }

    @Test
    void testBuildShortUrl_nullShortCode() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                UrlUtil.buildShortUrl("http://localhost:8080", null)
        );
        assertTrue(ex.getMessage().contains("shortCode"));
    }

    @Test
    void testBuildShortUrl_emptyShortCode() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                UrlUtil.buildShortUrl("http://localhost:8080", "  ")
        );
        assertTrue(ex.getMessage().contains("shortCode"));
    }
}
