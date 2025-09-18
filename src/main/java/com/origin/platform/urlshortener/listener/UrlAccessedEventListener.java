package com.origin.platform.urlshortener.listener;

import com.origin.platform.urlshortener.event.UrlAccessedEvent;
import com.origin.platform.urlshortener.exception.ResourceNotFoundException;
import com.origin.platform.urlshortener.model.AccessLog;
import com.origin.platform.urlshortener.model.UrlMapping;
import com.origin.platform.urlshortener.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UrlAccessedEventListener {

    private final UrlRepository repository;

    @EventListener
    @Transactional(propagation = Propagation.REQUIRED)
    public void handleUrlAccessedEvent(UrlAccessedEvent event) {
        UrlMapping mapping = repository.findByShortCode(event.code())
                .orElseThrow(() -> new ResourceNotFoundException("Short code not found: " + event.code()));

        log.info("UrlAccessedEvent handling url access code: {}", event.code());

        mapping.updateHit();
        AccessLog log = AccessLog.builder()
                .accessedAt(event.accessedAt())
                .ipAddress(event.ipAddress())
                .userAgent(event.userAgent())
                .referrer(event.referrer())
                .urlMapping(mapping)
                .build();
        mapping.getAccessLogs().add(log);
        repository.save(mapping);
    }
}
