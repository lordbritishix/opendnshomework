package com.lordbritishix.opendnshomework.urlshortener.service;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import javax.transaction.Transactional;
import com.lordbritishix.opendnshomework.urlshortener.model.Counter;
import com.lordbritishix.opendnshomework.urlshortener.model.UrlMap;
import com.lordbritishix.opendnshomework.urlshortener.repository.CounterRepository;
import com.lordbritishix.opendnshomework.urlshortener.repository.UrlMapRepository;
import com.lordbritishix.opendnshomework.urlshortener.utils.ShortenerUtil;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShortenerService {
    private final UrlMapRepository urlMapRepository;
    private final CounterRepository counterRepository;

    @Autowired
    public ShortenerService(
            UrlMapRepository urlMapRepository,
            CounterRepository counterRepository) {
        this.urlMapRepository = urlMapRepository;
        this.counterRepository = counterRepository;
    }

    @Transactional
    public URI generateShortUrl(String hostname, URI uri) {
        UrlMap found = urlMapRepository.findUrlMapByOriginalUrl(uri.toString());
        if (found != null) {
            return URI.create(found.getMappedUrl());
        }

        Counter counter = counterRepository.save(new Counter());
        long seed = counter.getId();
        String shortString = ShortenerUtil.createShortString(seed);
        URI mappedUrl = URI.create(hostname + "/" + shortString);
        long id = ShortenerUtil.toBase10(shortString);

        UrlMap urlMap = new UrlMap(id, uri.toString(), mappedUrl.toString());
        urlMapRepository.save(urlMap);

        return mappedUrl;
    }

    @Transactional
    public Optional<UrlMap> getShortUrl(String id) {
        long key = ShortenerUtil.toBase10(id);
        if (!urlMapRepository.existsById(key)) {
            return Optional.empty();
        }

        return Optional.of(urlMapRepository.getOne(key));
    }

    public boolean isValidUrl(String base64EncodedUrl) {
        try {
            String decodedUrl = new String(Base64.getUrlDecoder().decode(base64EncodedUrl), StandardCharsets.UTF_8);
            return UrlValidator.getInstance().isValid(decodedUrl);
        } catch (IllegalArgumentException e ) {
            return false;
        }
    }
}
