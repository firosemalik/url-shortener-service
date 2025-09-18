package com.origin.platform.urlshortener.service;

import com.origin.platform.urlshortener.model.UrlMapping;
import com.origin.platform.urlshortener.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UrlServiceComponentTest {

    @Autowired
    private UrlService urlService;

    @Autowired
    private UrlRepository urlRepository;

    @Test
    void shortenUrl_andGetOriginalUrl_success() {
        String originalUrl = "https://spring.io";
        String shortCode = urlService.shortenUrl(originalUrl);
        assertNotNull(shortCode);

        // The mapping should exist in the repository
        Optional<UrlMapping> mappingOpt = urlRepository.findByShortCode(shortCode);
        assertTrue(mappingOpt.isPresent());
        assertEquals(originalUrl, mappingOpt.get().getOriginalUrl());

        // getOriginalUrl should return the original and increment hit count
        String resolved = urlService.getOriginalUrl(shortCode, "127.0.0.1", "JUnit", "ref");
        assertEquals(originalUrl, resolved);
        UrlMapping urlMapping = urlRepository.findByShortCodeWithLogs(shortCode).get();
        assertEquals(1, urlMapping.getHitCount());
        assertNotNull(urlMapping.getAccessLogs());
        assertFalse(urlMapping.getAccessLogs().isEmpty());
    }

    @Test
    void shortenUrl_duplicateOriginalUrl_throwsException() {
        String originalUrl = "https://duplicate.com";
        urlService.shortenUrl(originalUrl);
        Exception ex = assertThrows(Exception.class, () -> urlService.shortenUrl(originalUrl));
        assertTrue(ex.getMessage().contains("already exists"));
    }

    @Test
    void getOriginalUrl_invalidShortCode_throwsException() {
        Exception ex = assertThrows(Exception.class,
                () -> urlService.getOriginalUrl("invalid", "ip", "ua", "ref"));
        assertTrue(ex.getMessage().contains("not found"));
    }
}