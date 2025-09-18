package com.origin.platform.urlshortener.service;

import com.origin.platform.urlshortener.dto.response.AccessLogResponse;
import com.origin.platform.urlshortener.mapper.AccessLogMapper;
import com.origin.platform.urlshortener.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessLogService {
    private final UrlRepository urlRepository;
    private final AccessLogMapper accessLogMapper;

    public Page<AccessLogResponse> getAccessLogs(String shortCode, Pageable pageable) {

        // This should be improved by passing the count to the query and retrieve only the requested size from db
        // Directly use the AccessLogRepository with @Query
        // A count query could be made to check partial response
        return Page.empty();
    }

}
