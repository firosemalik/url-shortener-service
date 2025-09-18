package com.origin.platform.urlshortener.mapper;

import com.origin.platform.urlshortener.dto.response.AccessLogResponse;
import com.origin.platform.urlshortener.model.AccessLog;
import org.springframework.stereotype.Component;

@Component
public class AccessLogMapper {
    public AccessLogResponse toAccessLogResponse(AccessLog log) {
        if (log == null) {
            throw new IllegalArgumentException("log must not be null");
        }
        return AccessLogResponse.builder()
                .accessedAt(log.getAccessedAt())
                .ipAddress(log.getIpAddress())
                .userAgent(log.getUserAgent())
                .referrer(log.getReferrer())
                .build();
    }
}
