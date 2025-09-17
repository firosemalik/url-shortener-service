package com.origin.platform.urlshortener.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class AccessLogResponse {
    private OffsetDateTime accessedAt;
    private String ipAddress;
    private String userAgent;
    private String referrer;
}
