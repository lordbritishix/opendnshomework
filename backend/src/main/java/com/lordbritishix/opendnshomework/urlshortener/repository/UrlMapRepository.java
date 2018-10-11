package com.lordbritishix.opendnshomework.urlshortener.repository;

import com.lordbritishix.opendnshomework.urlshortener.model.UrlMap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlMapRepository extends JpaRepository<UrlMap, Long> {
    UrlMap findUrlMapByOriginalUrl(String originalUrl);
}
