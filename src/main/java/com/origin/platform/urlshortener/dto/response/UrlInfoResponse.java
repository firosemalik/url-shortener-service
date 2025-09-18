package com.origin.platform.urlshortener.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlInfoResponse {
    private String originalUrl;
    private String shortCode;
    private int hitCount;
    private OffsetDateTime createdAt;
    private List<AccessLogResponse> accessLogs;
}
