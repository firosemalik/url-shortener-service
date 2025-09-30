package com.origin.platform.urlshortener.service;

import com.origin.platform.urlshortener.config.HashidProperties;
import com.origin.platform.urlshortener.exception.ResourceAlreadyExistException;
import com.origin.platform.urlshortener.exception.ResourceNotFoundException;
import com.origin.platform.urlshortener.model.AccessLog;
import com.origin.platform.urlshortener.model.UrlMapping;
import com.origin.platform.urlshortener.repository.UrlRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hashids.Hashids;
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

    @Transactional
    public String getOriginalUrl(String code, String ip, String userAgent, String referrer) {
        log.info("Resolving original URL for code: {} from IP: {} UA: {} Referrer: {}", code, ip, userAgent, referrer);
        // An in memory spring cache with caffeine or distributed like redis can be used to improve here
        final UrlMapping mapping = getUrlMappingCached(code);
        return mapping.getOriginalUrl();
    }

    @Transactional
    public void updateUrlAccess(String code, AccessLog log) {
        final UrlMapping urlMapping = repository.findByShortCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Short code not found: " + code));
        urlMapping.updateHit();
        log.setUrlMapping(urlMapping);
        urlMapping.getAccessLogs().add(log);
        repository.save(urlMapping);
    }

    @Transactional(readOnly = true)
    public Optional<UrlMapping> getUrlWithLogs(String code) {
        log.info("Fetching UrlMapping with logs for code: {}", code);
        return repository.findByShortCodeWithLogs(code);
    }


    private UrlMapping getUrlMappingCached(String code) {

        //This should ideally check the cache and return from cache if exists, if not make a db call update cache and return
        return repository.findByShortCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Short code not found: " + code));
    }
}
