package com.origin.platform.urlshortener.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccessLogPageResponse {
    private final java.util.List<AccessLogResponse> logs;
    private final boolean hasMore;
    private final long total;

    public AccessLogPageResponse(java.util.List<AccessLogResponse> logs, boolean hasMore, long total) {
        this.logs = logs;
        this.hasMore = hasMore;
        this.total = total;
    }

    public java.util.List<AccessLogResponse> getLogs() {
        return logs;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public long getTotal() {
        return total;
    }
}
