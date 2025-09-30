package com.origin.platform.urlshortener.listener;

import com.origin.platform.urlshortener.event.UrlAccessedEvent;
import com.origin.platform.urlshortener.model.AccessLog;
import com.origin.platform.urlshortener.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UrlAccessedEventListener {

    private final UrlService urlService;

    @EventListener
    public void handleUrlAccessedEvent(UrlAccessedEvent event) {
        log.info("UrlAccessedEvent handling url access code: {}", event.code());
        urlService.updateUrlAccess(event.code(), AccessLog.builder()
                .accessedAt(event.accessedAt())
                .ipAddress(event.ipAddress())
                .userAgent(event.userAgent())
                .referrer(event.referrer())
                .build());
    }
}
