package com.origin.platform.urlshortener.event;

import java.time.OffsetDateTime;

public record UrlAccessedEvent(
        String code,
        OffsetDateTime accessedAt,
        String ipAddress,
        String userAgent,
        String referrer) {
}
