package com.lordbritishix.opendnshomework.urlshortener.model;

public class ShortenResponse {
    private final String originalUrl;
    private final String shortenedUrl;
    private final String message;

    public ShortenResponse(String originalUrl, String shortenedUrl, String message) {
        this.originalUrl = originalUrl;
        this.shortenedUrl = shortenedUrl;
        this.message = message;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getShortenedUrl() {
        return shortenedUrl;
    }

    public String getMessage() {
        return message;
    }
}
