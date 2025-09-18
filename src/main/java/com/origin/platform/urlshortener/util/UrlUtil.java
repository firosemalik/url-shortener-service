package com.origin.platform.urlshortener.util;

public class UrlUtil {
    /**
     * Builds the full short URL from the endpoint path and short code.
     */
    public static String buildShortUrl(String endpointPath, String shortCode) {
        if (endpointPath == null || endpointPath.trim().isEmpty()) {
            throw new IllegalArgumentException("endpointPath must not be null or empty");
        }
        if (shortCode == null || shortCode.trim().isEmpty()) {
            throw new IllegalArgumentException("shortCode must not be null or empty");
        }
        StringBuilder sb = new StringBuilder(endpointPath);
        if (!endpointPath.endsWith("/")) {
            sb.append("/");
        }
        sb.append(shortCode);
        return sb.toString();
    }
}
