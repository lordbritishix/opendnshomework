package com.lordbritishix.opendnshomework.urlshortener.service;

import java.net.URI;
import java.util.Optional;
import javax.transaction.Transactional;
import com.lordbritishix.opendnshomework.urlshortener.model.UrlMap;
import com.lordbritishix.opendnshomework.urlshortener.repository.UrlMapRepository;
import com.lordbritishix.opendnshomework.urlshortener.utils.SequenceGenerator;
import com.lordbritishix.opendnshomework.urlshortener.utils.UrlShortenerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShortenerService {
    private final UrlMapRepository urlMapRepository;

    @Autowired
    public ShortenerService(UrlMapRepository urlMapRepositoryy) {
        this.urlMapRepository = urlMapRepositoryy;
    }

    @Transactional
    public URI generateShortUrl(String hostname, URI uri) {
        UrlMap found = urlMapRepository.findUrlMapByOriginalUrl(uri.toString());
        if (found != null) {
            return URI.create(found.getMappedUrl());
        }

        long seed = SequenceGenerator.nextId();
        String shortString = UrlShortenerUtils.createShortString(seed);
        URI mappedUrl = URI.create(hostname + "/" + shortString);

        // Collision - Throw an exception as we may need a better sequence generator
        if (urlMapRepository.existsById(shortString)) {
            throw new IllegalStateException("Id " + shortString + " with a different URL " + " is already present in the database.");
        }

        UrlMap urlMap = new UrlMap(shortString, uri.toString(), mappedUrl.toString());
        urlMapRepository.save(urlMap);

        return mappedUrl;
    }

    @Transactional
    public Optional<UrlMap> getShortUrl(String id) {
        return urlMapRepository.findById(id);
    }
}
