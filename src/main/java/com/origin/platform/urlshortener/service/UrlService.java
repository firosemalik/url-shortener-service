package com.origin.platform.urlshortener.service;

import com.origin.platform.urlshortener.config.HashidProperties;
import com.origin.platform.urlshortener.event.UrlAccessedEvent;
import com.origin.platform.urlshortener.exception.ResourceAlreadyExistException;
import com.origin.platform.urlshortener.exception.ResourceNotFoundException;
import com.origin.platform.urlshortener.model.UrlMapping;
import com.origin.platform.urlshortener.repository.UrlRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hashids.Hashids;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final UrlRepository repository;
    private final HashidProperties hashidProperties;
    private final ApplicationEventPublisher eventPublisher;

    private Hashids hashids;

    @PostConstruct
    public void init() {
        this.hashids = new Hashids(hashidProperties.getSalt(), hashidProperties.getMinLength());
    }

    @Transactional
    public String shortenUrl(String originalUrl) {
        log.info("Shortening originalUrl: {}", originalUrl);
        // Check if the originalUrl already exists
        if (repository.findByOriginalUrl(originalUrl).isPresent()) {
            throw new ResourceAlreadyExistException("URL already exists");
        }

        //Double save to generate ID with a temporary id before flush and then set actual short code
        UrlMapping mapping = UrlMapping.builder().originalUrl(originalUrl)
                .shortCode(UUID.randomUUID().toString())
                .createdAt(OffsetDateTime.now()).hitCount(0).build();

        // Entity id used to avoid duplicates and collisions in a concurrent environment
        // hashed with a salt to make it unpredictable to avoid crawl
        mapping = repository.save(mapping);
        String shortCode = hashids.encode(mapping.getId());
        mapping.setShortCode(shortCode);
        repository.save(mapping);

        return shortCode;
    }

    @Transactional(readOnly = true)
    public String getOriginalUrl(String code, String ip, String userAgent, String referrer) {
        log.info("Resolving original URL for code: {} from IP: {} UA: {} Referrer: {}", code, ip, userAgent, referrer);

        // An in memory spring cache with caffeine or distributed like redis can be used to improve here

        UrlMapping mapping = getUrlMappingCached(code);

        // The call to update the hit and access log should be asynchronous and in a separate transaction
        // Emit event instead of direct save, this is just to demonstrate but not the final solution
        eventPublisher.publishEvent(new UrlAccessedEvent(
                code,
                OffsetDateTime.now(),
                ip,
                userAgent,
                referrer
        ));
        // Simple to use spring event with @Async and handle the update, but the trade-off is managing thread pool

        return mapping.getOriginalUrl();
    }

    private UrlMapping getUrlMappingCached(String code) {
        UrlMapping mapping = repository.findByShortCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Short code not found: " + code));
        return mapping;
    }

    @Transactional(readOnly = true)
    public Optional<UrlMapping> getUrlWithLogs(String code) {
        log.info("Fetching UrlMapping with logs for code: {}", code);
        return repository.findByShortCodeWithLogs(code);
    }
}
