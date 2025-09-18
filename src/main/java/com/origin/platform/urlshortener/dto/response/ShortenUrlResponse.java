package com.origin.platform.urlshortener.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ShortenUrlResponse {
    private String originalUrl;
    private String shortUrl;
}
