package com.origin.platform.urlshortener.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class UrlInfoResponse {
    private String originalUrl;
    private String shortCode;
    private int hitCount;
    private OffsetDateTime createdAt;
    private List<AccessLogResponse> accessLogs;
}
