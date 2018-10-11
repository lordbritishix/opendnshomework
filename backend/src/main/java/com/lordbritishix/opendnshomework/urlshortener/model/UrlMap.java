package com.lordbritishix.opendnshomework.urlshortener.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UrlMap {
    @Id
    private long id;
    private String originalUrl;
    private String mappedUrl;

    public UrlMap() {
    }

    public UrlMap(long id, String originalUrl, String mappedUrl) {
        this.id = id;
        this.originalUrl = originalUrl;
        this.mappedUrl = mappedUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getMappedUrl() {
        return mappedUrl;
    }

    public void setMappedUrl(String mappedUrl) {
        this.mappedUrl = mappedUrl;
    }
}