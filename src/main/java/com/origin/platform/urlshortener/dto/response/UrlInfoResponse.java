package com.origin.platform.urlshortener.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlInfoResponse {
    private String originalUrl;
    private String shortCode;
    private int hitCount;
    private OffsetDateTime createdAt;
}
