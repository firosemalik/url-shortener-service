package com.origin.platform.urlshortener.controller;

import com.origin.platform.urlshortener.dto.request.ShortenUrlRequest;
import com.origin.platform.urlshortener.dto.response.AccessLogPageResponse;
import com.origin.platform.urlshortener.dto.response.AccessLogResponse;
import com.origin.platform.urlshortener.dto.response.ShortenUrlResponse;
import com.origin.platform.urlshortener.dto.response.UrlInfoResponse;
import com.origin.platform.urlshortener.event.UrlAccessedEvent;
import com.origin.platform.urlshortener.exception.ResourceNotFoundException;
import com.origin.platform.urlshortener.mapper.UrlMapper;
import com.origin.platform.urlshortener.service.AccessLogService;
import com.origin.platform.urlshortener.service.UrlService;
import com.origin.platform.urlshortener.util.UrlUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/urls")
@RequiredArgsConstructor
@Slf4j
public class UrlController {


    private final UrlService urlService;
    private final AccessLogService accessLogService;
    private final UrlMapper urlMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${urlshortener.endpoint.path:http://localhost:8080/}")
    private String endpointPath;

    @PostMapping
    public ResponseEntity<ShortenUrlResponse> createShortUrl(@Valid @RequestBody ShortenUrlRequest request) {
        log.info("Shortening URL: {}", request.getOriginalUrl());
        String shortCode = urlService.shortenUrl(request.getOriginalUrl());
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

        log.info("Redirect requested for code: {}", code);

        String ip = request.getRemoteAddr();
        String originalUrl = urlService.getOriginalUrl(code, ip, userAgent, referrer);

        // This is just to demonstrate but not the final solution
        // The call to update the hit and access log should be asynchronous and in a separate transaction
        // Emit event instead of direct save
        eventPublisher.publishEvent(new UrlAccessedEvent(
                code,
                OffsetDateTime.now(),
                ip,
                userAgent,
                referrer
        ));
        // Simple to use spring event with @Async and handle the update, but the trade-off is managing thread pool

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    @GetMapping("/{code}")
    public ResponseEntity<EntityModel<UrlInfoResponse>> getUrlInfo(
            @PathVariable String code) {

        log.info("Fetching URL info for code: {}", code);

        var urlMapping = urlService.getUrlWithLogs(code)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Short code not found"));

        UrlInfoResponse response = urlMapper.toUrlInfoResponse(urlMapping);
        EntityModel<UrlInfoResponse> model = EntityModel.of(response);
        model.add(
                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(UrlController.class)
                                .getAccessLogs(code, 0, 20)
                ).withRel("accessLogs")
        );
        return ResponseEntity.ok(model);
    }

    @GetMapping("/{code}/access-logs")
    public ResponseEntity<AccessLogPageResponse> getAccessLogs(
            @PathVariable String code,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("Fetching access logs for code: {} page: {} size: {}", code, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<AccessLogResponse> logPage = accessLogService.getAccessLogs(code, pageable);
        boolean hasMore = logPage.hasNext();
        return ResponseEntity.ok(
                new AccessLogPageResponse(logPage.getContent(), hasMore, logPage.getTotalElements())
        );
    }
}
