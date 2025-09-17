package com.origin.platform.urlshortener.controller;

import com.origin.platform.urlshortener.dto.request.ShortenUrlRequest;
import com.origin.platform.urlshortener.dto.response.AccessLogResponse;
import com.origin.platform.urlshortener.dto.response.ShortenUrlResponse;
import com.origin.platform.urlshortener.dto.response.UrlInfoResponse;
import com.origin.platform.urlshortener.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/shortener/urls")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService service;

    @PostMapping
    public ResponseEntity<ShortenUrlResponse> createShortUrl(@Valid @RequestBody ShortenUrlRequest request) {
        String shortCode = service.shortenUrl(request.getOriginalUrl());
        String shortUrl = "http://localhost:8085/" + shortCode;

        ShortenUrlResponse response = ShortenUrlResponse.builder()
                .originalUrl(request.getOriginalUrl())
                .shortUrl(shortUrl)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{code}/redirect")
    public ResponseEntity<Void> redirect(
            @PathVariable String code,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            @RequestHeader(value = "Referer", required = false) String referrer,
            HttpServletRequest request) {

        String ip = request.getRemoteAddr();
        String originalUrl = service.getOriginalUrl(code, ip, userAgent, referrer);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    @GetMapping("/{code}")
    public ResponseEntity<UrlInfoResponse> getUrlInfo(
            @PathVariable String code,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        var urlMapping = service.getUrlWithLogs(code, PageRequest.of(page, size))
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Short code not found"));

        UrlInfoResponse response = UrlInfoResponse.builder()
                .originalUrl(urlMapping.getOriginalUrl())
                .shortCode(urlMapping.getShortCode())
                .hitCount(urlMapping.getHitCount())
                .createdAt(urlMapping.getCreatedAt())
                .accessLogs(urlMapping.getAccessLogs().stream().map(log ->
                        AccessLogResponse.builder()
                                .accessedAt(log.getAccessedAt())
                                .ipAddress(log.getIpAddress())
                                .userAgent(log.getUserAgent())
                                .referrer(log.getReferrer())
                                .build()
                ).toList())
                .build();

        return ResponseEntity.ok(response);
    }
}
