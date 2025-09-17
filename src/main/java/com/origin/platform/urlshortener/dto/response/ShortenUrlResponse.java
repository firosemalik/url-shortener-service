package com.origin.platform.urlshortener.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShortenUrlResponse {
    private String originalUrl;
    private String shortUrl;
}
