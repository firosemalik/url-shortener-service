package com.origin.platform.urlshortener.service;

import com.origin.platform.urlshortener.config.HashidProperties;
import com.origin.platform.urlshortener.exception.ResourceAlreadyExistException;
import com.origin.platform.urlshortener.exception.ResourceNotFoundException;
import com.origin.platform.urlshortener.model.AccessLog;
import com.origin.platform.urlshortener.model.UrlMapping;
import com.origin.platform.urlshortener.repository.UrlRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.hashids.Hashids;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
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
        UrlMapping mapping = repository.findByShortCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Short code not found: " + code));
        mapping.setHitCount(mapping.getHitCount() + 1);
        AccessLog log = AccessLog.builder()
                .accessedAt(OffsetDateTime.now())
                .ipAddress(ip)
                .userAgent(userAgent)
                .referrer(referrer)
                .urlMapping(mapping)
                .build();

        mapping.getAccessLogs().add(log);
        repository.save(mapping);

        return mapping.getOriginalUrl();
    }

    @Transactional(readOnly = true)
    public Optional<UrlMapping> getUrlWithLogs(String code) {
        return repository.findByShortCodeWithLogs(code);
    }
}
