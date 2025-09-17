package com.origin.platform.urlshortener.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class ShortenUrlRequest {
    @NotBlank
    @URL(protocol = "https", message = "Invalid URL format")
    private String originalUrl;
}
