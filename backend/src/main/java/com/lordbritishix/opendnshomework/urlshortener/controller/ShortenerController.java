package com.lordbritishix.opendnshomework.urlshortener.controller;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import com.lordbritishix.opendnshomework.urlshortener.model.ShortenResponse;
import com.lordbritishix.opendnshomework.urlshortener.model.UrlMap;
import com.lordbritishix.opendnshomework.urlshortener.service.ShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/shortener")
public class ShortenerController {
    private final ShortenerService shortenerService;

    @Value("${service.hostname}")
    private String serviceHostname;

    @Autowired
    public ShortenerController(ShortenerService shortenerService) {
        this.shortenerService = shortenerService;
    }

    @PostMapping("/{base64EncodedUrl}")
    public ResponseEntity<ShortenResponse> shorten(@PathVariable String base64EncodedUrl) {
        if (!shortenerService.isValidUrl(base64EncodedUrl)) {
            return new ResponseEntity<>(new ShortenResponse(base64EncodedUrl, null,
                    "The provided URL is not valid. The URL must be base64-encoded and must conform with RFC 1738"), HttpStatus.BAD_REQUEST);
        }

        String decodedUrl = new String(Base64.getUrlDecoder().decode(base64EncodedUrl), StandardCharsets.UTF_8);
        URI shortenedUrl = shortenerService.generateShortUrl(serviceHostname, URI.create(decodedUrl));
        ShortenResponse response = new ShortenResponse(decodedUrl, shortenedUrl.toString(), "");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShortenResponse> getMapping(@PathVariable String id) {
        Optional<UrlMap> urlMap = shortenerService.getShortUrl(id);

        if (!urlMap.isPresent()) {
            return new ResponseEntity<>(new ShortenResponse(null, null,
                    "The provided id was not found"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new ShortenResponse(urlMap.get().getOriginalUrl(), urlMap.get().getMappedUrl(), ""), HttpStatus.BAD_REQUEST);
    }
}
