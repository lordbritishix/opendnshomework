package com.lordbritishix.opendnshomework.urlshortener.repository;

import com.lordbritishix.opendnshomework.urlshortener.model.PhishInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhishInfoRepository extends JpaRepository<PhishInfo, Long> {
    boolean existsByUrl(String url);
}
