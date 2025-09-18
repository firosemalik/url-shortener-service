package com.origin.platform.urlshortener.mapper;

import com.origin.platform.urlshortener.model.AccessLog;
import com.origin.platform.urlshortener.dto.response.AccessLogResponse;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AccessLogMapperTest {
    private final AccessLogMapper accessLogMapper = new AccessLogMapper();

    @Test
    void testToAccessLogResponse() {
        OffsetDateTime now = OffsetDateTime.now();
        AccessLog log = AccessLog.builder()
                .accessedAt(now)
                .ipAddress("127.0.0.1")
                .userAgent("JUnit")
                .referrer("http://referrer.com")
                .build();

        AccessLogResponse response = accessLogMapper.toAccessLogResponse(log);
        assertNotNull(response);
        assertEquals(now, response.getAccessedAt());
        assertEquals("127.0.0.1", response.getIpAddress());
        assertEquals("JUnit", response.getUserAgent());
        assertEquals("http://referrer.com", response.getReferrer());
    }

    @Test
    void testToAccessLogResponse_NullLog() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> accessLogMapper.toAccessLogResponse(null));
        assertTrue(ex.getMessage().contains("log"));
    }
}
