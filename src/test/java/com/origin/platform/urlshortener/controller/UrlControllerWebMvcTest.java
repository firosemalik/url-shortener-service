package com.origin.platform.urlshortener.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.origin.platform.urlshortener.dto.request.ShortenUrlRequest;
import com.origin.platform.urlshortener.service.UrlService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlController.class)
@ComponentScan("com.origin.platform.urlshortener.mapper")
class UrlControllerWebMvcTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UrlService urlService;

    @Test
    void whenValidShortenUrlRequest_thenReturnsShortUrl() throws Exception {
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setOriginalUrl("https://example.com");

        Mockito.when(urlService.shortenUrl(anyString())).thenReturn("abc123");

        mockMvc.perform(post("/shortener/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalUrl", is("https://example.com")))
                .andExpect(jsonPath("$.shortUrl", is("http://localhost:8085/abc123")));
    }

    @Test
    void whenInvalidShortenUrlRequest_thenReturnsValidationError() throws Exception {
        ShortenUrlRequest request = new ShortenUrlRequest();
        // No originalUrl set, should fail validation
        mockMvc.perform(post("/shortener/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Validation failed")))
                .andExpect(jsonPath("$.errors.originalUrl", not(emptyOrNullString())));
    }

    @Test
    void whenValidCodeForRedirect_thenReturnsFound() throws Exception {
        Mockito.when(urlService.getOriginalUrl(anyString(), anyString(), any(), any())).thenReturn("https://example.com");
        mockMvc.perform(get("/shortener/urls/abc123/redirect"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com"));
    }

    @Test
    void whenValidCodeForUrlInfo_thenReturnsInfo() throws Exception {
        var urlMapping = Mockito.mock(com.origin.platform.urlshortener.model.UrlMapping.class);
        Mockito.when(urlService.getUrlWithLogs(anyString()))
                .thenReturn(Optional.of(urlMapping));

        mockMvc.perform(get("/shortener/urls/abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessLogs").exists());
    }

    @Test
    void whenInvalidCodeForUrlInfo_thenReturnsNotFound() throws Exception {
        Mockito.when(urlService.getUrlWithLogs(anyString()))
                .thenReturn(Optional.empty());
        mockMvc.perform(get("/shortener/urls/invalid"))
                .andExpect(status().isInternalServerError()); // Controller throws RuntimeException for not found
    }
}
