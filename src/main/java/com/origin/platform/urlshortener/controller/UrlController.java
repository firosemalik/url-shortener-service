package com.origin.platform.urlshortener.controller;

import com.origin.platform.urlshortener.dto.request.ShortenUrlRequest;
import com.origin.platform.urlshortener.dto.response.ShortenUrlResponse;
import com.origin.platform.urlshortener.dto.response.UrlInfoResponse;
import com.origin.platform.urlshortener.mapper.AccessLogMapper;
import com.origin.platform.urlshortener.mapper.UrlMapper;
import com.origin.platform.urlshortener.service.UrlService;
import com.origin.platform.urlshortener.util.UrlUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/shortener/urls")
@RequiredArgsConstructor

public class UrlController {

    private final UrlService service;
    private final UrlMapper urlMapper;
    private final AccessLogMapper accessLogMapper;

    @Value("${urlshortener.endpoint.path:http://localhost:8080/}")
    private String endpointPath;

    @PostMapping
    public ResponseEntity<ShortenUrlResponse> createShortUrl(@Valid @RequestBody ShortenUrlRequest request) {
        String shortCode = service.shortenUrl(request.getOriginalUrl());
        String shortUrl = UrlUtil.buildShortUrl(endpointPath, shortCode);

        ShortenUrlResponse response = urlMapper.toShortenUrlResponse(request.getOriginalUrl(), shortUrl);
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
            @PathVariable String code) {

        //TODO Add hateos and work on this part to return access log separately
        var urlMapping = service.getUrlWithLogs(code)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Short code not found"));

        UrlInfoResponse response = urlMapper.toUrlInfoResponse(urlMapping);
        // Set accessLogs using AccessLogMapper
        if (urlMapping.getAccessLogs() != null) {
            response.setAccessLogs(urlMapping.getAccessLogs().stream()
                    .map(accessLogMapper::toAccessLogResponse)
                    .toList());
        }
        return ResponseEntity.ok(response);
    }
}
