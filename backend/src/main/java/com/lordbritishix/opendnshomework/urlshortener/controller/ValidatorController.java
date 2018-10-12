package com.lordbritishix.opendnshomework.urlshortener.controller;


import com.lordbritishix.opendnshomework.urlshortener.model.ValidatorResponse;
import com.lordbritishix.opendnshomework.urlshortener.service.PhishTankService;
import com.lordbritishix.opendnshomework.urlshortener.utils.UrlShortenerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for validating if a url is a phishing site
 */
@RestController
@RequestMapping(value = "/validate")
public class ValidatorController {
    private final PhishTankService phishTankService;

    @Autowired
    public ValidatorController(PhishTankService phishTankService) {
        this.phishTankService = phishTankService;
    }

    @GetMapping("/{base64EncodedUrl}")
    public ResponseEntity<ValidatorResponse> getMapping(@PathVariable String base64EncodedUrl) {
        if (!UrlShortenerUtils.isValidUrl(base64EncodedUrl)) {
            return new ResponseEntity<>(new ValidatorResponse(
                    "The provided URL is not valid. The URL must be base64-encoded and must conform with RFC 1738", false), HttpStatus.BAD_REQUEST);
        }

        boolean isPhishingSite = phishTankService.isUrlSafe(UrlShortenerUtils.getUrl(base64EncodedUrl));
        ValidatorResponse response = new ValidatorResponse(null, isPhishingSite);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
