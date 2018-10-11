package com.lordbritishix.opendnshomework.urlshortener.service;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ShortenerServiceTest {
    private ShortenerService shortenerService;

    @Before
    public void setup() {
        shortenerService = new ShortenerService(null, null);
    }

    @Test
    public void isValidReturnsFalseIfNotBase64Encoded() {
        String notBase64EncodedUrl = "http://google.com";
        assertFalse(shortenerService.isValidUrl(notBase64EncodedUrl));
    }

    @Test
    public void isValidReturnsTrueIfBase64EncodedAndValid() {
        // http://google.com/search
        String validBase64EncodedUrl = "aHR0cDovL2dvb2dsZS5jb20vc2VhcmNo";
        assertTrue(shortenerService.isValidUrl(validBase64EncodedUrl));
    }

    @Test
    public void isValidReturnsFalseIfBase64EncodedButNotValid() {
        // google.com/search
        String notValidBase64EncodedUrl = "Z29vZ2xlLmNvbS9zZWFyY2g=";
        assertFalse(shortenerService.isValidUrl(notValidBase64EncodedUrl));
    }
}
