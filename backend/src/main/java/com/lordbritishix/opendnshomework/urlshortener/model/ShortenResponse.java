package com.lordbritishix.opendnshomework.urlshortener.model;

import lombok.Data;

@Data
public class ShortenResponse {
    private final String originalUrl;
    private final String shortenedUrl;
    private final String message;

    public ShortenResponse(String originalUrl, String shortenedUrl, String message) {
        this.originalUrl = originalUrl;
        this.shortenedUrl = shortenedUrl;
        this.message = message;
    }
}
